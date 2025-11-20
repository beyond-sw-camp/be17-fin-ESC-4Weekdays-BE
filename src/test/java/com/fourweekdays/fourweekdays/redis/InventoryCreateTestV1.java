package com.fourweekdays.fourweekdays.redis;

import com.fourweekdays.fourweekdays.inventory.model.entity.Inventory;
import com.fourweekdays.fourweekdays.inventory.repository.InventoryRepository;
import com.fourweekdays.fourweekdays.location.model.entity.Location;
import com.fourweekdays.fourweekdays.location.repository.LocationRepository;
import com.fourweekdays.fourweekdays.product.model.entity.Product;
import com.fourweekdays.fourweekdays.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@ActiveProfiles("test")
@TestPropertySource(properties = {
        "redisson.enabled=false",
        "redis.sentinel.nodes="
})
@SpringBootTest
class InventoryCreateTestV1 {

    @Autowired private InventoryRepository inventoryRepository;
    @Autowired private ProductRepository productRepository;
    @Autowired private LocationRepository locationRepository;

    private Product productA;
    private Product productB;
    private Long locationId;

    @BeforeEach
    void setUp() {

        inventoryRepository.deleteAll();
        productRepository.deleteAll();
        locationRepository.deleteAll();

        Location loc = locationRepository.save(
                Location.builder()
                        .zone("Z1")
                        .section("A")
                        .vendorId(1L)
                        .capacity(50000)
                        .usedCapacity(0)
                        .status(com.fourweekdays.fourweekdays.location.model.entity.LocationStatus.AVAILABLE)
                        .build()
        );
        locationId = loc.getId();

        productA = productRepository.save(
                Product.builder()
                        .productCode("A")
                        .name("A상품")
                        .unit("EA")
                        .unitPrice(100L)
                        .status(com.fourweekdays.fourweekdays.product.model.entity.ProductStatus.ACTIVE)
                        .vendor(null)
                        .build()
        );

        productB = productRepository.save(
                Product.builder()
                        .productCode("B")
                        .name("B상품")
                        .unit("EA")
                        .unitPrice(100L)
                        .status(com.fourweekdays.fourweekdays.product.model.entity.ProductStatus.ACTIVE)
                        .vendor(null)
                        .build()
        );
    }

    @Test
    void V1_동시성제어_없음() throws Exception {

        int threads = 200;
        ExecutorService executor = Executors.newFixedThreadPool(50);
        CountDownLatch latch = new CountDownLatch(threads);

        for (int i = 0; i < threads; i++) {
            final boolean isA = (i % 2 == 0);
            executor.submit(() -> {
                try {
                    Long pid = isA ? productA.getId() : productB.getId();
                    String lot = isA ? "LOT-A" : "LOT-B";

                    Inventory inv = inventoryRepository
                            .findByProductAndLocationAndLotWithLock(pid, locationId, lot)
                            .orElse(null);

                    if (inv == null) {
                        inv = Inventory.builder()
                                .product(productRepository.findById(pid).get())
                                .location(locationRepository.findById(locationId).get())
                                .lotNumber(lot)
                                .quantity(1)
                                .build();
                        inventoryRepository.save(inv);
                    } else {
                        inv.increaseQuantity(1);
                    }

                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executor.shutdown();

        int sumA = inventoryRepository.findByProductId(productA.getId())
                .stream().mapToInt(Inventory::getQuantity).sum();

        int sumB = inventoryRepository.findByProductId(productB.getId())
                .stream().mapToInt(Inventory::getQuantity).sum();

        System.out.println("🎯 NO LOCK RESULT");
        System.out.println("A 재고 = " + sumA);
        System.out.println("B 재고 = " + sumB);
    }
}
