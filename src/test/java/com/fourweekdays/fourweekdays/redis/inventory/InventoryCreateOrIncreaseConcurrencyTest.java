package com.fourweekdays.fourweekdays.redis.inventory;

import com.fourweekdays.fourweekdays.config.RedissonTestConfig;
import com.fourweekdays.fourweekdays.inventory.model.entity.Inventory;
import com.fourweekdays.fourweekdays.inventory.repository.InventoryRepository;
import com.fourweekdays.fourweekdays.inventory.service.InventoryService;
import com.fourweekdays.fourweekdays.location.model.entity.Location;
import com.fourweekdays.fourweekdays.location.model.entity.LocationStatus;
import com.fourweekdays.fourweekdays.location.repository.LocationRepository;
import com.fourweekdays.fourweekdays.order.repository.OrderProductItemRepository;
import com.fourweekdays.fourweekdays.order.repository.OrderRepository;
import com.fourweekdays.fourweekdays.outbound.repository.OutboundInventoryHistoryRepository;
import com.fourweekdays.fourweekdays.outbound.repository.OutboundProductItemRepository;
import com.fourweekdays.fourweekdays.outbound.repository.OutboundRepository;
import com.fourweekdays.fourweekdays.product.model.entity.Product;
import com.fourweekdays.fourweekdays.product.model.entity.ProductStatus;
import com.fourweekdays.fourweekdays.product.repository.ProductRepository;
import com.fourweekdays.fourweekdays.vendor.model.entity.Vendor;
import com.fourweekdays.fourweekdays.vendor.model.entity.VendorStatus;
import com.fourweekdays.fourweekdays.vendor.repository.VendorRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

@SpringBootTest
@ActiveProfiles("test")
@Import(RedissonTestConfig.class)
class InventoryCreateOrIncreaseConcurrencyTest {

    @Autowired
    private InventoryService inventoryService;
    @Autowired
    private InventoryRepository inventoryRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private LocationRepository locationRepository;
    @Autowired
    private VendorRepository vendorRepository;
    @Autowired
    private OutboundInventoryHistoryRepository outboundInventoryHistoryRepository;
    @Autowired
    private OutboundProductItemRepository outboundProductItemRepository;
    @Autowired
    private OutboundRepository outboundRepository;
    @Autowired
    private OrderProductItemRepository orderProductItemRepository;
    @Autowired
    private OrderRepository orderRepository;

    private Long productAId;
    private Long productBId;
    private Long locationAId;
    private Long locationBId;

    private final String lotA = "LOT-A";
    private final String lotB = "LOT-B";

    @BeforeEach
    void setUp() {
        outboundInventoryHistoryRepository.deleteAll();
        outboundProductItemRepository.deleteAll();
        outboundRepository.deleteAll();
        orderProductItemRepository.deleteAll();
        orderRepository.deleteAll();

        // 깨끗한 상태 보장
        inventoryRepository.deleteAll();
        productRepository.deleteAll();
        locationRepository.deleteAll();
        vendorRepository.deleteAll();

        // 공통 Vendor
        Vendor vendor = Vendor.builder()
                .vendorCode("V-TEST-001")
                .name("테스트 공급업체")
                .phoneNumber("010-1111-2222")
                .email("test@vendor.com")
                .description("테스트 벤더")
                .status(VendorStatus.ACTIVE)
                .address(null)
                .build();
        vendor = vendorRepository.save(vendor);

        // Location A / B (서로 다른 Location)
        Location locationA = Location.builder()
                .zone("Z1")
                .section("A")
                .vendorId(vendor.getId())
                .capacity(100000)
                .status(LocationStatus.AVAILABLE)
                .description("테스트 위치 A")
                .build();
        locationA = locationRepository.save(locationA);

        Location locationB = Location.builder()
                .zone("Z1")
                .section("B")
                .vendorId(vendor.getId())
                .capacity(100000)
                .status(LocationStatus.AVAILABLE)
                .description("테스트 위치 B")
                .build();
        locationB = locationRepository.save(locationB);

        // Product A / B (서로 다른 상품)
        Product productA = Product.builder()
                .name("테스트 상품 A")
                .productCode("PRD-TEST-A")
                .unit("EA")
                .unitPrice(1000L)
                .description("테스트 product A")
                .status(ProductStatus.ACTIVE)
                .vendor(vendor)
                .build();
        productA = productRepository.save(productA);

        Product productB = Product.builder()
                .name("테스트 상품 B")
                .productCode("PRD-TEST-B")
                .unit("EA")
                .unitPrice(2000L)
                .description("테스트 product B")
                .status(ProductStatus.ACTIVE)
                .vendor(vendor)
                .build();
        productB = productRepository.save(productB);

        this.productAId = productA.getId();
        this.productBId = productB.getId();
        this.locationAId = locationA.getId();
        this.locationBId = locationB.getId();
    }

    @Test
    void inventoryCreateOrIncrease_multiProduct_multiLocation_concurrency_B() throws Exception {

        int usersPerProduct = 100;   // 상품 A, B 각각 100명씩
        int qtyPerUser = 10;         // 사용자 1명당 10개씩 입고
        int totalThreads = usersPerProduct * 2;

        ExecutorService executor = Executors.newFixedThreadPool(32);
        CountDownLatch latch = new CountDownLatch(totalThreads);

        AtomicInteger successA = new AtomicInteger(0);
        AtomicInteger successB = new AtomicInteger(0);
        AtomicInteger failLock = new AtomicInteger(0);
        AtomicInteger etcError = new AtomicInteger(0);

        long start = System.currentTimeMillis();

        // Product A + Location A + LOT-A
        for (int i = 0; i < usersPerProduct; i++) {
            executor.submit(() -> {
                try {
                    inventoryService.createOrIncreaseInventory(
                            productAId,
                            locationAId,
                            lotA,
                            qtyPerUser,
                            null  // inboundId: 현재 로직에서 사용 안 함
                    );
                    successA.incrementAndGet();
                } catch (Exception e) {
                    String msg = e.getMessage();
                    if (msg != null && (msg.contains("락") || msg.toLowerCase().contains("lock"))) {
                        failLock.incrementAndGet();
                    } else {
                        etcError.incrementAndGet();
                    }
                } finally {
                    latch.countDown();
                }
            });
        }

        // Product B + Location B + LOT-B
        for (int i = 0; i < usersPerProduct; i++) {
            executor.submit(() -> {
                try {
                    inventoryService.createOrIncreaseInventory(
                            productBId,
                            locationBId,
                            lotB,
                            qtyPerUser,
                            null
                    );
                    successB.incrementAndGet();
                } catch (Exception e) {
                    String msg = e.getMessage();
                    if (msg != null && (msg.contains("락") || msg.toLowerCase().contains("lock"))) {
                        failLock.incrementAndGet();
                    } else {
                        etcError.incrementAndGet();
                    }
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executor.shutdown();

        long duration = System.currentTimeMillis() - start;

        // 최종 재고 조회
        int finalQtyA = inventoryRepository.findByProductId(productAId)
                .stream()
                .mapToInt(Inventory::getQuantity)
                .sum();

        int finalQtyB = inventoryRepository.findByProductId(productBId)
                .stream()
                .mapToInt(Inventory::getQuantity)
                .sum();

        int expectedQtyA = usersPerProduct * qtyPerUser;
        int expectedQtyB = usersPerProduct * qtyPerUser;

        double qps = totalThreads / (duration / 1000.0);

        // ===== 로그 출력 =====
        System.out.println("==============================================================");
        System.out.println("🌐 상품 A/B 재고 증가 동시성 테스트 (B 방식 - 상품/로케이션 분리)");
        System.out.printf("총 스레드 수              : %d (A: %d, B: %d)\n",
                totalThreads, usersPerProduct, usersPerProduct);
        System.out.printf("상품 A 최종 재고          : %d (기대값: %d)\n", finalQtyA, expectedQtyA);
        System.out.printf("상품 B 최종 재고          : %d (기대값: %d)\n\n", finalQtyB, expectedQtyB);

        System.out.printf("🟢 성공(A)                : %d\n", successA.get());
        System.out.printf("🟢 성공(B)                : %d\n", successB.get());
        System.out.printf("🔒 락 획득 실패           : %d\n", failLock.get());
        System.out.printf("⚠️ 기타 오류             : %d\n\n", etcError.get());

        System.out.printf("⏱  총 소요 시간          : %d ms (%.2f sec)\n", duration, duration / 1000.0);
        System.out.printf("🚀 평균 처리 속도(QPS)     : %.2f req/sec\n", qps);
        System.out.println("==============================================================\n");

        // ===== 검증 =====
        // 상품 A/B 각각 기대 재고 수량이 정확히 맞는지
        Assertions.assertThat(finalQtyA).isEqualTo(expectedQtyA);
        Assertions.assertThat(finalQtyB).isEqualTo(expectedQtyB);

        // 이번 시나리오는 lock 경합이 거의 없도록 설계했으므로 락 실패/기타 오류는 0 으로 기대
        Assertions.assertThat(failLock.get()).isZero();
        Assertions.assertThat(etcError.get()).isZero();
    }
}
