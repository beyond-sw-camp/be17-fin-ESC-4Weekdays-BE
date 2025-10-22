package com.fourweekdays.fourweekdays.inventory.service;

import com.fourweekdays.fourweekdays.inbound.model.entity.InboundProduct;
import com.fourweekdays.fourweekdays.inventory.model.entity.Inventory;
import com.fourweekdays.fourweekdays.inventory.model.entity.Location;
import com.fourweekdays.fourweekdays.inventory.repository.InventoryRepository;
import com.fourweekdays.fourweekdays.product.model.entity.Product;
import com.fourweekdays.fourweekdays.vendor.model.entity.Vendor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class InventoryService {

    private final InventoryRepository inventoryRepository;
    private final LocationService locationService;

    /**
     * 적치 완료 후 재고 반영
     * 이 시점에는 Location이 이미 할당되어 있어야 함
     */
    @Transactional
    public void increaseStock(InboundProduct inboundProduct) {
        Product product = inboundProduct.getProduct();
        Vendor vendor = product.getVendor();

        // Location은 이미 할당되어 있다고 가정 (적치 작업 시 할당됨)
        Location location = locationService.getAssignedLocation(vendor);

        // Inventory 조회 또는 생성
        Inventory inventory = inventoryRepository.findByProductAndLocation(product, location)
                .orElseGet(() -> Inventory.create(product, location, 0L));

        // 재고 증가
        inventory.increase(inboundProduct.getReceivedQuantity());
        inventoryRepository.save(inventory);
    }
}
