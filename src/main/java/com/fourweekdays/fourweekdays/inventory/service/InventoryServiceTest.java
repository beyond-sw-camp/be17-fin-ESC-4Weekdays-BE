package com.fourweekdays.fourweekdays.inventory.service;

import com.fourweekdays.fourweekdays.inventory.model.entity.Inventory;
import com.fourweekdays.fourweekdays.inventory.repository.InventoryRepository;
import com.fourweekdays.fourweekdays.location.model.entity.Location;
import com.fourweekdays.fourweekdays.location.model.entity.LocationStatus;
import com.fourweekdays.fourweekdays.location.repository.LocationRepository;
import com.fourweekdays.fourweekdays.product.model.entity.Product;
import com.fourweekdays.fourweekdays.product.model.entity.ProductStatus;
import com.fourweekdays.fourweekdays.product.repository.ProductRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class InventoryServiceTest {

    @Autowired
    private InventoryRepository inventoryRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private LocationRepository locationRepository;

    private Product productA;
    private Product productB;
    private Long locationId;

    @PostConstruct
    public void setUp() {
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
                        .status(LocationStatus.AVAILABLE)
                        .build()
        );
        locationId = loc.getId();

        productA = productRepository.save(
                Product.builder()
                        .productCode("A")
                        .name("A상품")
                        .unit("EA")
                        .unitPrice(100L)
                        .status(ProductStatus.ACTIVE)
                        .build()
        );

        productB = productRepository.save(
                Product.builder()
                        .productCode("B")
                        .name("B상품")
                        .unit("EA")
                        .unitPrice(100L)
                        .status(ProductStatus.ACTIVE)
                        .build()
        );
    }

    // V1 동시성제어 없음
    public void runV1Test() throws InterruptedException {
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
    }

    // V2 synchronized 테스트
    public void runV2Test() throws InterruptedException {
        int threadCount = 200;
        ExecutorService pool = Executors.newFixedThreadPool(64);
        CountDownLatch latch = new CountDownLatch(threadCount);

        for (int i = 0; i < threadCount; i++) {
            pool.submit(() -> {
                try {
                    increaseInventory("PRD-A", 10);
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        pool.shutdown();
    }

    // V2-1 서버 2대 synchronized 깨짐
    public void runV2_1Test() throws InterruptedException {
        int threadCount = 200;
        ExecutorService pool = Executors.newFixedThreadPool(64);
        CountDownLatch latch = new CountDownLatch(threadCount);

        for (int i = 0; i < threadCount; i++) {
            boolean useServer1 = (i % 2 == 0);
            pool.submit(() -> {
                try {
                    if (useServer1) {
                        increaseInventory("PRD-A", 10);
                    } else {
                        increaseInventory("PRD-B", 10);
                    }
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        pool.shutdown();
    }

    // V3 비관적 락 테스트
    public void runV3Test() throws InterruptedException {
        int threadCount = 200;
        ExecutorService pool = Executors.newFixedThreadPool(64);
        CountDownLatch latch = new CountDownLatch(threadCount);

        AtomicInteger success = new AtomicInteger();
        AtomicInteger fail = new AtomicInteger();

        for (int i = 0; i < threadCount; i++) {
            final int idx = i;

            pool.submit(() -> {
                try {
                    if (idx % 2 == 0) {
                        increaseInventoryWithLock("PRD-A", "LOT-A", 1);
                    } else {
                        increaseInventoryWithLock("PRD-B", "LOT-B", 1);
                    }
                    success.incrementAndGet();
                } catch (Exception e) {
                    fail.incrementAndGet();
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        pool.shutdown();
    }

    // 동기화된 방식으로 재고 증가 처리
    private synchronized void increaseInventory(String productCode, int qty) {
        Product product = productRepository.findByProductCode(productCode)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));

        List<Inventory> inventories = inventoryRepository.findByProductId(product.getId());

        if (inventories.isEmpty()) {
            Inventory newInventory = Inventory.builder()
                    .product(product)
                    .location(locationRepository.findById(locationId).get())
                    .quantity(qty)
                    .build();
            inventoryRepository.save(newInventory);
        } else {
            Inventory inventory = inventories.get(0);
            inventory.increaseQuantity(qty);
            inventoryRepository.save(inventory);
        }
    }

    // 비관적 락을 사용한 재고 증가 처리
    private void increaseInventoryWithLock(String productCode, String lot, int qty) {
        Product product = productRepository.findByProductCode(productCode)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));

        Inventory inventory = inventoryRepository.findByProductAndLocationAndLotWithLock(product.getId(), locationId, lot)
                .orElse(null);

        if (inventory == null) {
            inventory = Inventory.builder()
                    .product(product)
                    .location(locationRepository.findById(locationId).get())
                    .lotNumber(lot)
                    .quantity(qty)
                    .build();
            inventoryRepository.save(inventory);
        } else {
            inventory.increaseQuantity(qty);
            inventoryRepository.save(inventory);
        }
    }
}
