package com.fourweekdays.fourweekdays.redis.outbound;

import com.fourweekdays.fourweekdays.config.RedissonTestConfig;
import com.fourweekdays.fourweekdays.inventory.model.entity.Inventory;
import com.fourweekdays.fourweekdays.inventory.repository.InventoryRepository;
import com.fourweekdays.fourweekdays.location.model.entity.Location;
import com.fourweekdays.fourweekdays.location.model.entity.LocationStatus;
import com.fourweekdays.fourweekdays.location.repository.LocationRepository;
import com.fourweekdays.fourweekdays.member.model.entity.AuthStatus;
import com.fourweekdays.fourweekdays.member.model.entity.Member;
import com.fourweekdays.fourweekdays.member.model.entity.MemberRole;
import com.fourweekdays.fourweekdays.member.repository.MemberRepository;
import com.fourweekdays.fourweekdays.order.model.entity.Order;
import com.fourweekdays.fourweekdays.order.model.entity.OrderProductItem;
import com.fourweekdays.fourweekdays.order.model.entity.OrderStatus;
import com.fourweekdays.fourweekdays.order.repository.OrderProductItemRepository;
import com.fourweekdays.fourweekdays.order.repository.OrderRepository;
import com.fourweekdays.fourweekdays.outbound.model.entity.Outbound;
import com.fourweekdays.fourweekdays.outbound.model.entity.OutboundProductItem;
import com.fourweekdays.fourweekdays.outbound.model.entity.OutboundStatus;
import com.fourweekdays.fourweekdays.outbound.model.entity.OutboundType;
import com.fourweekdays.fourweekdays.outbound.repository.OutboundInventoryHistoryRepository;
import com.fourweekdays.fourweekdays.outbound.repository.OutboundProductItemRepository;
import com.fourweekdays.fourweekdays.outbound.repository.OutboundRepository;
import com.fourweekdays.fourweekdays.outbound.service.OutboundService;
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

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Import(RedissonTestConfig.class)
class OutboundMultiProductConcurrencyTest {

    @Autowired private OutboundService outboundService;

    @Autowired private InventoryRepository inventoryRepository;
    @Autowired private OutboundRepository outboundRepository;
    @Autowired private OutboundProductItemRepository outboundProductItemRepository;
    @Autowired private OutboundInventoryHistoryRepository outboundInventoryHistoryRepository;

    @Autowired private OrderRepository orderRepository;
    @Autowired private OrderProductItemRepository orderProductItemRepository;

    @Autowired private ProductRepository productRepository;
    @Autowired private VendorRepository vendorRepository;
    @Autowired private LocationRepository locationRepository;

    @Autowired private MemberRepository memberRepository;

    private Product productA;
    private Product productB;
    private Long locationId;
    private Member manager;

    @BeforeEach
    void setUp() {

        outboundInventoryHistoryRepository.deleteAll();
        outboundProductItemRepository.deleteAll();
        outboundRepository.deleteAll();
        orderProductItemRepository.deleteAll();
        orderRepository.deleteAll();
        inventoryRepository.deleteAll();
        productRepository.deleteAll();
        vendorRepository.deleteAll();
        locationRepository.deleteAll();
        memberRepository.deleteAll();

        manager = memberRepository.save(
                Member.builder()
                        .name("관리자")
                        .email("admin@test.com")
                        .password("pw")
                        .role(MemberRole.ADMIN)
                        .status(AuthStatus.ACTIVE)
                        .build()
        );

        Vendor vendor = vendorRepository.save(
                Vendor.builder()
                        .vendorCode("V001")
                        .name("테스트 벤더")
                        .status(VendorStatus.ACTIVE)
                        .build()
        );

        Location location = locationRepository.save(
                Location.builder()
                        .zone("Z1")
                        .section("A")
                        .vendorId(vendor.getId())
                        .capacity(5000)
                        .usedCapacity(2000)
                        .status(LocationStatus.AVAILABLE)
                        .build()
        );
        locationId = location.getId();

        productA = productRepository.save(
                Product.builder()
                        .productCode("PRD-A")
                        .name("상품A")
                        .unit("EA")
                        .unitPrice(100L)
                        .status(ProductStatus.ACTIVE)
                        .vendor(vendor)
                        .build()
        );

        productB = productRepository.save(
                Product.builder()
                        .productCode("PRD-B")
                        .name("상품B")
                        .unit("EA")
                        .unitPrice(100L)
                        .status(ProductStatus.ACTIVE)
                        .vendor(vendor)
                        .build()
        );

        inventoryRepository.save(
                Inventory.builder()
                        .product(productA)
                        .location(location)
                        .lotNumber("LOT-A")
                        .quantity(1000)
                        .build()
        );

        inventoryRepository.save(
                Inventory.builder()
                        .product(productB)
                        .location(location)
                        .lotNumber("LOT-B")
                        .quantity(1000)
                        .build()
        );
    }

    @Test
    void multiProductOutboundConcurrencyTest() throws Exception {

        List<Long> outboundIdsA = new ArrayList<>();
        List<Long> outboundIdsB = new ArrayList<>();

        createOutboundRequests(productA.getId(), outboundIdsA, manager, 100);
        createOutboundRequests(productB.getId(), outboundIdsB, manager, 100);

        int initialA = 1000;
        int initialB = 1000;

        int total = outboundIdsA.size() + outboundIdsB.size();

        ExecutorService pool = Executors.newFixedThreadPool(64);
        CountDownLatch latch = new CountDownLatch(total);

        AtomicInteger success = new AtomicInteger();
        AtomicInteger fail = new AtomicInteger();
        AtomicInteger lockFailA = new AtomicInteger();
        AtomicInteger lockFailB = new AtomicInteger();

        long start = System.currentTimeMillis();

        for (Long id : outboundIdsA) {
            pool.submit(() -> {
                try {
                    boolean ok = outboundService.approveOutbound(id);
                    if (ok) success.incrementAndGet();
                    else fail.incrementAndGet();
                } catch (Exception e) {
                    lockFailA.incrementAndGet();
                } finally {
                    latch.countDown();
                }
            });
        }

        for (Long id : outboundIdsB) {
            pool.submit(() -> {
                try {
                    boolean ok = outboundService.approveOutbound(id);
                    if (ok) success.incrementAndGet();
                    else fail.incrementAndGet();
                } catch (Exception e) {
                    lockFailB.incrementAndGet();
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

        printResult(
                total,
                initialA,
                initialB,
                stockA,
                stockB,
                success.get(),
                fail.get(),
                (end - start),
                lockFailA.get(),
                lockFailB.get()
        );

        assertThat(success.get()).isEqualTo(200);
        assertThat(stockA).isEqualTo(0);
        assertThat(stockB).isEqualTo(0);
    }

    private void printResult(
            int total,
            int initialStockA,
            int initialStockB,
            int stockA,
            int stockB,
            int success,
            int fail,
            long timeMs,
            int lockFailA,
            int lockFailB
    ) {
        System.out.println("\n===================== 🔥 다중 상품 출고 동시성 테스트 =====================");

        System.out.println("📌 총 요청한 사용자 수     : " + total);

        System.out.println("📦 초기 재고(A)            : " + initialStockA);
        System.out.println("📦 초기 재고(B)            : " + initialStockB);

        System.out.println("🍎 남은 재고(A)            : " + stockA);
        System.out.println("🍏 남은 재고(B)            : " + stockB);

        System.out.println();
        System.out.println("🟢 승인 성공               : " + success + " (" + calcPercent(success, total) + "%)");
        System.out.println("🔴 승인 실패               : " + fail + " (" + calcPercent(fail, total) + "%)");

        System.out.println();
        System.out.println("🔒 락 획득 실패(A)         : " + lockFailA);
        System.out.println("🔒 락 획득 실패(B)         : " + lockFailB);

        System.out.println();
        System.out.println("📤 총 요청 수              : " + total);
        System.out.println("⏱  총 소요 시간           : " + timeMs + " ms (" + (timeMs / 1000.0) + " sec)");

        double qps = total / (timeMs / 1000.0);
        System.out.printf("🚀 평균 처리 속도(QPS)     : %.2f req/sec\n", qps);

        System.out.println("======================================================================\n");
    }

    private String calcPercent(int value, int total) {
        return String.format("%.2f", (value * 100.0 / total));
    }

    private void createOutboundRequests(Long productId, List<Long> targetList,
                                        Member manager, int count) {

        Product product = productRepository.findById(productId).orElseThrow();

        for (int i = 0; i < count; i++) {

            Order order = Order.builder()
                    .orderCode("ORD-" + product.getProductCode() + "-" + i)
                    .orderDate(LocalDateTime.now())
                    .dueDate(LocalDateTime.now().plusDays(1))
                    .status(OrderStatus.APPROVED)
                    .build();

            OrderProductItem item = OrderProductItem.builder()
                    .product(product)
                    .orderedQuantity(10)
                    .build();

            order.addItem(item);
            order = orderRepository.save(order);

            Outbound outbound = Outbound.builder()
                    .outboundCode("OB-" + product.getProductCode() + "-" + i)
                    .status(OutboundStatus.REQUESTED)
                    .outboundType(OutboundType.SALE)
                    .order(order)
                    .outboundManager(manager)
                    .scheduledDate(LocalDateTime.now())
                    .build();

            outbound = outboundRepository.save(outbound);

            outboundProductItemRepository.save(
                    OutboundProductItem.builder()
                            .outbound(outbound)
                            .product(product)
                            .orderProductItem(item)
                            .orderedQuantity(10)
                            .build()
            );

            targetList.add(outbound.getId());
        }
    }
}
