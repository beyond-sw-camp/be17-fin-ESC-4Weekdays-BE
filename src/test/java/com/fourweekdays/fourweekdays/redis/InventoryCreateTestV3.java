package com.fourweekdays.fourweekdays.redis;

import com.fourweekdays.fourweekdays.config.RedissonTestConfig;
import com.fourweekdays.fourweekdays.inbound.model.entity.Inbound;
import com.fourweekdays.fourweekdays.inbound.model.entity.InboundProduct;
import com.fourweekdays.fourweekdays.inbound.model.entity.InboundStatus;
import com.fourweekdays.fourweekdays.inbound.repository.InboundRepository;
import com.fourweekdays.fourweekdays.inventory.model.entity.Inventory;
import com.fourweekdays.fourweekdays.inventory.repository.InventoryRepository;
import com.fourweekdays.fourweekdays.inventory.service.InventoryService;
import com.fourweekdays.fourweekdays.location.model.entity.Location;
import com.fourweekdays.fourweekdays.location.model.entity.LocationStatus;
import com.fourweekdays.fourweekdays.location.repository.LocationRepository;
import com.fourweekdays.fourweekdays.product.model.entity.Product;
import com.fourweekdays.fourweekdays.product.model.entity.ProductStatus;
import com.fourweekdays.fourweekdays.product.repository.ProductRepository;
import com.fourweekdays.fourweekdays.vendor.model.entity.Vendor;
import com.fourweekdays.fourweekdays.vendor.model.entity.VendorStatus;
import com.fourweekdays.fourweekdays.vendor.repository.VendorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.CountDownLatch;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Import(RedissonTestConfig.class)
public class InventoryCreateTestV3 {

    @Autowired private InventoryService inventoryService;

    @Autowired private ProductRepository productRepository;
    @Autowired private VendorRepository vendorRepository;

    @Autowired private LocationRepository locationRepository;

    @Autowired private InventoryRepository inventoryRepository;

    @Autowired private InboundRepository inboundRepository;

    private Product productA;
    private Product productB;
    private Location location;

    @BeforeEach
    void setUp() {

        inventoryRepository.deleteAll();
        inboundRepository.deleteAll();
        productRepository.deleteAll();
        vendorRepository.deleteAll();
        locationRepository.deleteAll();

        Vendor vendor = vendorRepository.save(
                Vendor.builder()
                        .vendorCode("V001")
                        .name("테스트벤더")
                        .status(VendorStatus.ACTIVE)
                        .build()
        );

        location = locationRepository.save(
                Location.builder()
                        .zone("Z1")
                        .section("A")
                        .capacity(99999)
                        .usedCapacity(0)
                        .vendorId(vendor.getId())
                        .status(LocationStatus.AVAILABLE)
                        .build()
        );

        productA = productRepository.save(
                Product.builder()
                        .productCode("PRD-A")
                        .name("상품A")
                        .status(ProductStatus.ACTIVE)
                        .unit("EA")
                        .unitPrice(100L)
                        .vendor(vendor)
                        .build()
        );

        productB = productRepository.save(
                Product.builder()
                        .productCode("PRD-B")
                        .name("상품B")
                        .status(ProductStatus.ACTIVE)
                        .unit("EA")
                        .unitPrice(100L)
                        .vendor(vendor)
                        .build()
        );
    }

    @Test
    void 비관적락_V3() throws Exception {

        int threadCount = 200;
        ExecutorService pool = Executors.newFixedThreadPool(64);
        CountDownLatch latch = new CountDownLatch(threadCount);

        AtomicInteger success = new AtomicInteger();
        AtomicInteger fail = new AtomicInteger();

        int qty = 1;

        long start = System.currentTimeMillis();

        for (int i = 0; i < threadCount; i++) {
            final int idx = i;

            pool.submit(() -> {
                try {
                    if (idx % 2 == 0) {
                        Long inboundA = createInbound(productA, "LOT-A");
                        inventoryService.createInventoryFromInbound(inboundA, location.getLocationCode());
                    } else {
                        Long inboundB = createInbound(productB, "LOT-B");
                        inventoryService.createInventoryFromInbound(inboundB, location.getLocationCode());
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

        long end = System.currentTimeMillis();

        int stockA = inventoryRepository.findByProductId(productA.getId())
                .stream().mapToInt(Inventory::getQuantity).sum();

        int stockB = inventoryRepository.findByProductId(productB.getId())
                .stream().mapToInt(Inventory::getQuantity).sum();

        printResult(threadCount, 0, 0, stockA, stockB, success.get(), fail.get(), end - start);

        assertThat(stockA).isEqualTo(100);
        assertThat(stockB).isEqualTo(100);
    }

    // ===================== ⚙️ Inbound 생성 =====================
    private Long createInbound(Product product, String lot) {

        Inbound inbound = Inbound.builder()
                .inboundCode("IB-" + lot)
                .status(InboundStatus.CREATED)
                .description("테스트 입고")
                .build();

        InboundProduct ip = InboundProduct.builder()
                .inbound(inbound)
                .product(product)
                .receivedQuantity(1)
                .lotNumber(lot)
                .build();

        inbound.getProducts().add(ip);

        return inboundRepository.save(inbound).getId();
    }

    // ====================== 출력 포맷 ============================
    private void printResult(
            int total,
            int initialA,
            int initialB,
            int stockA,
            int stockB,
            int success,
            int fail,
            long timeMs
    ) {
        System.out.println("\n===================== 🔥 V3(DB 비관적 락) 재고 증가 테스트 =====================");

        System.out.println("📌 총 요청 수              : " + total);
        System.out.println("📦 초기 재고(A)            : " + initialA);
        System.out.println("📦 초기 재고(B)            : " + initialB);

        System.out.println("🍎 남은 재고(A)            : " + stockA);
        System.out.println("🍏 남은 재고(B)            : " + stockB);

        System.out.println("🟢 성공                   : " + success);
        System.out.println("🔴 실패                   : " + fail);

        System.out.println("⏱  총 소요 시간           : " + timeMs + " ms");
        System.out.printf("🚀 평균 처리 속도(QPS)     : %.2f req/sec\n", total / (timeMs / 1000.0));

        System.out.println("======================================================================\n");
    }
}
