package com.fourweekdays.fourweekdays.inventory.service;


import com.fourweekdays.fourweekdays.inbound.model.entity.Inbound;
import com.fourweekdays.fourweekdays.inbound.model.entity.InboundProduct;
import com.fourweekdays.fourweekdays.inbound.repository.InboundRepository;
import com.fourweekdays.fourweekdays.inventory.exception.InventoryException;
import com.fourweekdays.fourweekdays.inventory.model.dto.request.InventorySearchRequest;
import com.fourweekdays.fourweekdays.inventory.model.dto.response.InventoryReadDto;
import com.fourweekdays.fourweekdays.inventory.model.dto.response.ProductInventoryResponse;
import com.fourweekdays.fourweekdays.inventory.model.entity.Inventory;
import com.fourweekdays.fourweekdays.inventory.repository.InventoryRepository;
import com.fourweekdays.fourweekdays.location.exception.LocationException;
import com.fourweekdays.fourweekdays.location.model.entity.Location;
import com.fourweekdays.fourweekdays.location.repository.LocationRepository;
import com.fourweekdays.fourweekdays.product.model.entity.Product;
import com.fourweekdays.fourweekdays.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static com.fourweekdays.fourweekdays.inventory.exception.InventoryExceptionType.*;
import static com.fourweekdays.fourweekdays.location.exception.LocationExceptionType.LOCATION_NOT_FOUND;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class InventoryService {

    private final InventoryRepository inventoryRepository;
    private final ProductRepository productRepository;
    private final LocationRepository locationRepository;
    private final InboundRepository inboundRepository;
    private final RedissonClient redissonClient;

    @Transactional
    public void createInventoryFromInbound(Long inboundId, String locationCode) {
        // Inbound 조회
        Inbound inbound = inboundRepository.findById(inboundId)
                .orElseThrow(() -> new InventoryException(INVENTORY_NOT_FOUND));

        // Location 조회
        Location location = locationRepository.findByLocationCodeWithLock(locationCode)
                .orElseThrow(() -> new LocationException(LOCATION_NOT_FOUND));

        // 모든 InboundProduct를 순회하며 재고 생성
        for (InboundProduct inboundProduct : inbound.getProducts()) {
            createOrIncreaseInventory(
                    inboundProduct.getProduct().getId(),
                    location.getId(),
                    inboundProduct.getLotNumber(),
                    inboundProduct.getReceivedQuantity(),
                    inbound.getId()
            );
        }
    }

    @Transactional
    public void decreaseInventory(Long productId, Long locationId, String lotNumber, Integer quantity) {
        Inventory inventory = inventoryRepository.findByProductAndLocationAndLotWithLock(productId, locationId, lotNumber)
                .orElseThrow(() -> new InventoryException(INVENTORY_NOT_FOUND));

        inventory.decreaseQuantity(quantity);

        // 재고가 0이 되면 삭제 (선택적)
        if (inventory.getQuantity() == 0) {
            inventoryRepository.delete(inventory);
        }
    }

    public Page<InventoryReadDto> searchInventory(InventorySearchRequest request, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Inventory> inventories = inventoryRepository.searchInventory(pageable, request);

        return inventories.map(InventoryReadDto::from);
    }

    public Page<ProductInventoryResponse> searchInventoryByProduct(InventorySearchRequest request, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return inventoryRepository.searchInventoryByProduct(pageable, request);
    }

    public ProductInventoryResponse productInventoryDetail(String productCode) {
        return inventoryRepository.findDetailByProductCode(productCode)
                .orElseThrow(() -> new InventoryException(INVENTORY_NOT_FOUND));
    }

    public int getTotalQuantityByProduct(Long productId) {
        return inventoryRepository.findByProductId(productId)
                .stream()
                .mapToInt(Inventory::getQuantity)
                .sum();

    }

    public int getTotalQuantityByLocation(Long locationId) {
        return inventoryRepository.findByLocationId(locationId)
                .stream()
                .mapToInt(Inventory::getQuantity)
                .sum();
    }

    @Transactional
    public void createOrIncreaseInventory(Long productId, Long locationId, String lotNumber,
                                          Integer quantity, Long inboundId) {

        // Lock 키 생성
        String inventoryLockKey = String.format("inventory:lock:%d:%d:%s", productId, locationId, lotNumber);
        String locationLockKey = String.format("location:lock:%d", locationId);

        RLock inventoryLock = redissonClient.getLock(inventoryLockKey);
        RLock locationLock = redissonClient.getLock(locationLockKey);

        try {
            // inventory Lock 획득
            boolean inventoryLocked = inventoryLock.tryLock(10, 5, TimeUnit.SECONDS);
            if (!inventoryLocked) {
                throw new InventoryException(LOCK_ACQUISITION_FAILED);
            }

            // location Lock 획득
            boolean locationLocked = locationLock.tryLock(10, 5, TimeUnit.SECONDS);
            if (!locationLocked) {
                inventoryLock.unlock();
                throw new InventoryException(LOCK_ACQUISITION_FAILED);
            }

            // 재고 조회
            Optional<Inventory> existingInventory =
                    inventoryRepository.findByProductAndLocationAndLotWithLock(productId, locationId, lotNumber);

            if (existingInventory.isPresent()) {
                // 기존 재고가 있으면 수량 증가
                Inventory inventory = existingInventory.get();
                inventory.increaseQuantity(quantity);

            } else {
                // 없으면 새로운 재고 생성
                Product product = productRepository.findById(productId)
                        .orElseThrow(() -> new InventoryException(INVENTORY_NOT_FOUND));

                Location location = locationRepository.findById(locationId)
                        .orElseThrow(() -> new InventoryException(LOCATION_NOT_FOUND));

                // Location 용량 증가도 Location 락으로 보호됨
                location.increaseUsedCapacity(quantity);

                Inventory newInventory = Inventory.builder()
                        .product(product)
                        .location(location)
                        .lotNumber(lotNumber)
                        .quantity(quantity)
                        .build();

                inventoryRepository.save(newInventory);
            }

        } catch (InterruptedException e) {
            throw new InventoryException(LOCK_INTERRUPTED);

        } finally {
            // 락 순서대로 해제 (location → inventory)
            if (locationLock.isHeldByCurrentThread()) {
                locationLock.unlock();
            }
            if (inventoryLock.isHeldByCurrentThread()) {
                inventoryLock.unlock();
            }
        }
    }
}