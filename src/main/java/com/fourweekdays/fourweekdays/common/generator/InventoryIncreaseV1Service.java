package com.fourweekdays.fourweekdays.common.generator;

import com.fourweekdays.fourweekdays.inventory.model.entity.Inventory;
import com.fourweekdays.fourweekdays.inventory.repository.InventoryRepository;
import com.fourweekdays.fourweekdays.location.model.entity.Location;
import com.fourweekdays.fourweekdays.location.repository.LocationRepository;
import com.fourweekdays.fourweekdays.product.model.entity.Product;
import com.fourweekdays.fourweekdays.product.repository.ProductRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InventoryIncreaseV1Service {

    private final InventoryRepository inventoryRepository;
    private final ProductRepository productRepository;
    private final LocationRepository locationRepository;

    @Transactional
    public void increase(Long productId, Long locationId, String lot, int qty) {

        Inventory inv = inventoryRepository.findByProductAndLocationAndLotWithLock(
                productId, locationId, lot
        ).orElse(null);

        if (inv == null) {
            Product product = productRepository.findById(productId).orElseThrow();
            Location loc = locationRepository.findById(locationId).orElseThrow();

            inv = Inventory.builder()
                    .product(product)
                    .location(loc)
                    .lotNumber(lot)
                    .quantity(qty)
                    .build();
        } else {
            inv.increaseQuantity(qty);
        }

        inventoryRepository.save(inv);
    }
}
