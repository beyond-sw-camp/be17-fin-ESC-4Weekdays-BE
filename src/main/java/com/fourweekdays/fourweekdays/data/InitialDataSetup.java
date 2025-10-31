package com.fourweekdays.fourweekdays.data;

import com.fourweekdays.fourweekdays.common.vo.Address;
import com.fourweekdays.fourweekdays.inbound.model.entity.Inbound;
import com.fourweekdays.fourweekdays.inbound.model.entity.InboundProduct;
import com.fourweekdays.fourweekdays.inbound.model.entity.InboundStatus;
import com.fourweekdays.fourweekdays.inbound.repository.InboundRepository;
import com.fourweekdays.fourweekdays.inventory.model.entity.Inventory;
import com.fourweekdays.fourweekdays.inventory.repository.InventoryRepository;
import com.fourweekdays.fourweekdays.location.model.entity.Location;
import com.fourweekdays.fourweekdays.location.model.entity.LocationStatus;
import com.fourweekdays.fourweekdays.location.repository.LocationRepository;
import com.fourweekdays.fourweekdays.member.model.entity.AuthStatus;
import com.fourweekdays.fourweekdays.member.model.entity.Member;
import com.fourweekdays.fourweekdays.member.model.entity.MemberRole;
import com.fourweekdays.fourweekdays.member.repository.MemberRepository;
import com.fourweekdays.fourweekdays.product.model.entity.Product;
import com.fourweekdays.fourweekdays.product.model.entity.ProductStatus;
import com.fourweekdays.fourweekdays.product.repository.ProductRepository;
import com.fourweekdays.fourweekdays.purchaseorder.model.entity.PurchaseOrder;
import com.fourweekdays.fourweekdays.purchaseorder.model.entity.PurchaseOrderProduct;
import com.fourweekdays.fourweekdays.purchaseorder.model.entity.PurchaseOrderStatus;
import com.fourweekdays.fourweekdays.purchaseorder.repository.PurchaseOrderRepository;
import com.fourweekdays.fourweekdays.tasks.model.entity.InspectionTask;
import com.fourweekdays.fourweekdays.tasks.model.entity.Task;
import com.fourweekdays.fourweekdays.tasks.model.entity.TaskCategory;
import com.fourweekdays.fourweekdays.tasks.model.entity.TaskStatus;
import com.fourweekdays.fourweekdays.tasks.repository.InspectionTaskRepository;
import com.fourweekdays.fourweekdays.tasks.repository.TaskRepository;
import com.fourweekdays.fourweekdays.vendor.model.entity.Vendor;
import com.fourweekdays.fourweekdays.vendor.model.entity.VendorStatus;
import com.fourweekdays.fourweekdays.vendor.repository.VendorRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;

/**
 * 초기 더미 데이터 생성
 *
 * 사용법:
 * 1. 이 파일을 그대로 두고 애플리케이션 실행
 * 2. 데이터가 생성되면 이 파일 삭제하거나 @Configuration 주석 처리
 * 3. 끝!
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class InitialDataSetup {

    @Transactional
    @Bean
    CommandLineRunner initDatabase(
            VendorRepository vendorRepository,
            MemberRepository memberRepository,
            ProductRepository productRepository,
            PurchaseOrderRepository purchaseOrderRepository,
            InboundRepository inboundRepository,
            LocationRepository locationRepository,
            PasswordEncoder passwordEncoder,
            TaskRepository taskRepository,
            InspectionTaskRepository inspectionTaskRepository,
            InventoryRepository inventoryRepository
    ) {

        return args -> {
            // 이미 데이터가 있으면 건너뛰기
            if (vendorRepository.count() > 0) {
                log.info("데이터가 이미 존재합니다. 초기화를 건너뜁니다.");
                return;
            }

            log.info("========== 초기 데이터 생성 시작 ==========");

            // 1. 공급업체 (화장품 브랜드)
            Vendor vendor1 = vendorRepository.save(Vendor.builder()
                    .vendorCode("V-001")
                    .name("아모레퍼시픽")
                    .phoneNumber("02-6040-5114")
                    .email("contact@amorepacific.com")
                    .description("설화수, 라네즈 등 다양한 브랜드 보유")
                    .status(VendorStatus.ACTIVE)
                    .address(Address.builder()
                            .zipcode("04386")
                            .street("서울특별시 용산구 한강대로 100")
                            .detail("아모레퍼시픽 본사")
                            .build())
                    .build());

            Vendor vendor2 = vendorRepository.save(Vendor.builder()
                    .vendorCode("V-002")
                    .name("LG생활건강")
                    .phoneNumber("02-6924-5114")
                    .email("info@lgcare.com")
                    .description("더페이스샵, 빌리프 등")
                    .status(VendorStatus.ACTIVE)
                    .address(Address.builder()
                            .zipcode("07795")
                            .street("서울특별시 강서구 마곡중앙8로 71")
                            .detail("LG사이언스파크")
                            .build())
                    .build());

            Vendor vendor3 = vendorRepository.save(Vendor.builder()
                    .vendorCode("V-003")
                    .name("코스맥스")
                    .phoneNumber("02-2188-0114")
                    .email("support@cosmax.com")
                    .description("화장품 ODM 전문 기업")
                    .status(VendorStatus.ACTIVE)
                    .address(Address.builder()
                            .zipcode("13486")
                            .street("경기도 성남시 중원구 둔촌대로 505")
                            .detail("코스맥스 빌딩")
                            .build())
                    .build());

            Vendor vendor4 = vendorRepository.save(Vendor.builder()
                    .vendorCode("V-004")
                    .name("에이블씨엔씨")
                    .phoneNumber("031-789-2345")
                    .email("info@ablecnc.com")
                    .description("미샤 브랜드 운영")
                    .status(VendorStatus.ACTIVE)
                    .address(Address.builder()
                            .zipcode("16229")
                            .street("경기도 수원시 영통구 광교로 156")
                            .detail("광교비즈니스센터")
                            .build())
                    .build());

            Vendor vendor5 = vendorRepository.save(Vendor.builder()
                    .vendorCode("V-005")
                    .name("토니모리")
                    .phoneNumber("02-2140-5000")
                    .email("cs@tonymoly.com")
                    .description("색조 화장품 전문")
                    .status(VendorStatus.ACTIVE)
                    .build());

            Vendor vendor6 = vendorRepository.save(Vendor.builder()
                    .vendorCode("V-006")
                    .name("네이처리퍼블릭")
                    .phoneNumber("02-2051-0114")
                    .email("help@naturerepublic.com")
                    .description("자연주의 화장품")
                    .status(VendorStatus.INACTIVE)
                    .build());

            log.info("✓ 공급업체 6개 생성");

            // 2. 직원
            Member admin = memberRepository.save(Member.builder()
                    .email("admin@company.com")
                    .password(passwordEncoder.encode("admin1234"))
                    .name("관리자")
                    .phoneNumber("010-1111-1111")
                    .role(MemberRole.ADMIN)
                    .status(AuthStatus.ACTIVE)
                    .joinAt(LocalDateTime.now().minusYears(5))
                    .build());

            Member manager1 = memberRepository.save(Member.builder()
                    .email("manager1@company.com")
                    .password(passwordEncoder.encode("manager1234"))
                    .name("김매니저")
                    .phoneNumber("010-2222-2222")
                    .role(MemberRole.MANAGER)
                    .status(AuthStatus.ACTIVE)
                    .joinAt(LocalDateTime.now().minusYears(3))
                    .build());

            Member manager2 = memberRepository.save(Member.builder()
                    .email("manager2@company.com")
                    .password(passwordEncoder.encode("manager1234"))
                    .name("이매니저")
                    .phoneNumber("010-2223-3333")
                    .role(MemberRole.MANAGER)
                    .status(AuthStatus.ACTIVE)
                    .joinAt(LocalDateTime.now().minusYears(2))
                    .build());

            Member worker1 = memberRepository.save(Member.builder()
                    .email("worker1@company.com")
                    .password(passwordEncoder.encode("worker1234"))
                    .name("박작업자")
                    .phoneNumber("010-3333-3333")
                    .role(MemberRole.WORKER)
                    .status(AuthStatus.ACTIVE)
                    .joinAt(LocalDateTime.now().minusYears(2))
                    .build());

            Member worker2 = memberRepository.save(Member.builder()
                    .email("worker2@company.com")
                    .password(passwordEncoder.encode("worker1234"))
                    .name("최작업자")
                    .phoneNumber("010-4444-4444")
                    .role(MemberRole.WORKER)
                    .status(AuthStatus.ACTIVE)
                    .joinAt(LocalDateTime.now().minusYears(1))
                    .build());

            Member worker3 = memberRepository.save(Member.builder()
                    .email("worker3@company.com")
                    .password(passwordEncoder.encode("worker1234"))
                    .name("정작업자")
                    .phoneNumber("010-5555-5555")
                    .role(MemberRole.WORKER)
                    .status(AuthStatus.ACTIVE)
                    .joinAt(LocalDateTime.now().minusMonths(8))
                    .build());

            Member worker4 = memberRepository.save(Member.builder()
                    .email("worker4@company.com")
                    .password(passwordEncoder.encode("worker1234"))
                    .name("강작업자")
                    .phoneNumber("010-6666-6666")
                    .role(MemberRole.WORKER)
                    .status(AuthStatus.ACTIVE)
                    .joinAt(LocalDateTime.now().minusMonths(3))
                    .build());

            log.info("✓ 직원 7명 생성");

            // 3. 로케이션 (보관 장소)
            Location loc1 = locationRepository.save(Location.builder()
                    .zone("01")
                    .section("A")
                    .vendorId(vendor1.getId())
                    .capacity(20000)
                    .status(LocationStatus.AVAILABLE)
                    .description("아모레퍼시픽 전용 구역")
                    .build());

            Location loc2 = locationRepository.save(Location.builder()
                    .zone("01")
                    .section("B")
                    .vendorId(vendor1.getId())
                    .capacity(15000)
                    .status(LocationStatus.AVAILABLE)
                    .description("아모레퍼시픽 보조 구역")
                    .build());

            Location loc3 = locationRepository.save(Location.builder()
                    .zone("02")
                    .section("A")
                    .vendorId(vendor2.getId())
                    .capacity(18000)
                    .status(LocationStatus.AVAILABLE)
                    .description("LG생활건강 전용 구역")
                    .build());

            Location loc4 = locationRepository.save(Location.builder()
                    .zone("02")
                    .section("B")
                    .vendorId(vendor2.getId())
                    .capacity(15000)
                    .status(LocationStatus.AVAILABLE)
                    .description("LG생활건강 보조 구역")
                    .build());

            Location loc5 = locationRepository.save(Location.builder()
                    .zone("03")
                    .section("A")
                    .vendorId(vendor3.getId())
                    .capacity(25000)
                    .status(LocationStatus.AVAILABLE)
                    .description("코스맥스 대형 구역")
                    .build());

            Location loc6 = locationRepository.save(Location.builder()
                    .zone("04")
                    .section("A")
                    .vendorId(vendor4.getId())
                    .capacity(12000)
                    .status(LocationStatus.AVAILABLE)
                    .description("에이블씨엔씨(미샤) 구역")
                    .build());

            Location loc7 = locationRepository.save(Location.builder()
                    .zone("05")
                    .section("A")
                    .vendorId(vendor5.getId())
                    .capacity(10000)
                    .status(LocationStatus.AVAILABLE)
                    .description("토니모리 구역")
                    .build());

            Location loc8 = locationRepository.save(Location.builder()
                    .zone("06")
                    .section("A")
                    .vendorId(null)
                    .capacity(15000)
                    .status(LocationStatus.AVAILABLE)
                    .description("공용 구역")
                    .build());

            log.info("✓ 로케이션 8개 생성");

            // 4. 상품 (20개)
            Product product1 = productRepository.save(Product.builder()
                    .productCode("P-001")
                    .name("설화수 윤조에센스")
                    .description("한방 보습 에센스")
                    .unitPrice(80000L)
                    .vendor(vendor1)
                    .status(ProductStatus.ACTIVE)
                    .build());

            Product product2 = productRepository.save(Product.builder()
                    .productCode("P-002")
                    .name("라네즈 워터슬리핑마스크")
                    .description("수면 보습팩")
                    .unitPrice(27000L)
                    .vendor(vendor1)
                    .status(ProductStatus.ACTIVE)
                    .build());

            Product product3 = productRepository.save(Product.builder()
                    .productCode("P-003")
                    .name("설화수 자음생크림")
                    .description("고영양 안티에이징 크림")
                    .unitPrice(150000L)
                    .vendor(vendor1)
                    .status(ProductStatus.ACTIVE)
                    .build());

            Product product4 = productRepository.save(Product.builder()
                    .productCode("P-004")
                    .name("라네즈 크림스킨")
                    .description("촉촉한 크림 타입 스킨")
                    .unitPrice(35000L)
                    .vendor(vendor1)
                    .status(ProductStatus.ACTIVE)
                    .build());

            Product product5 = productRepository.save(Product.builder()
                    .productCode("P-005")
                    .name("더페이스샵 클렌징폼")
                    .description("저자극 클렌징폼")
                    .unitPrice(8000L)
                    .vendor(vendor2)
                    .status(ProductStatus.ACTIVE)
                    .build());

            Product product6 = productRepository.save(Product.builder()
                    .productCode("P-006")
                    .name("빌리프 아쿠아밤")
                    .description("수분 보습 크림")
                    .unitPrice(32000L)
                    .vendor(vendor2)
                    .status(ProductStatus.ACTIVE)
                    .build());

            Product product7 = productRepository.save(Product.builder()
                    .productCode("P-007")
                    .name("더페이스샵 선크림")
                    .description("SPF50+ PA+++ 자외선 차단제")
                    .unitPrice(15000L)
                    .vendor(vendor2)
                    .status(ProductStatus.ACTIVE)
                    .build());

            Product product8 = productRepository.save(Product.builder()
                    .productCode("P-008")
                    .name("빌리프 트루크림")
                    .description("진정 보습 크림")
                    .unitPrice(38000L)
                    .vendor(vendor2)
                    .status(ProductStatus.ACTIVE)
                    .build());

            Product product9 = productRepository.save(Product.builder()
                    .productCode("P-009")
                    .name("코스맥스 비타민C 세럼")
                    .description("고농축 비타민C 세럼")
                    .unitPrice(45000L)
                    .vendor(vendor3)
                    .status(ProductStatus.ACTIVE)
                    .build());

            Product product10 = productRepository.save(Product.builder()
                    .productCode("P-010")
                    .name("코스맥스 레티놀 크림")
                    .description("안티에이징 기능성 크림")
                    .unitPrice(52000L)
                    .vendor(vendor3)
                    .status(ProductStatus.ACTIVE)
                    .build());

            Product product11 = productRepository.save(Product.builder()
                    .productCode("P-011")
                    .name("코스맥스 나이아신아마이드 토너")
                    .description("미백 기능성 토너")
                    .unitPrice(28000L)
                    .vendor(vendor3)
                    .status(ProductStatus.ACTIVE)
                    .build());

            Product product12 = productRepository.save(Product.builder()
                    .productCode("P-012")
                    .name("미샤 쿠션")
                    .description("커버력 좋은 쿠션 파운데이션")
                    .unitPrice(12000L)
                    .vendor(vendor4)
                    .status(ProductStatus.ACTIVE)
                    .build());

            Product product13 = productRepository.save(Product.builder()
                    .productCode("P-013")
                    .name("미샤 비비크림")
                    .description("올인원 베이스 메이크업")
                    .unitPrice(9000L)
                    .vendor(vendor4)
                    .status(ProductStatus.ACTIVE)
                    .build());

            Product product14 = productRepository.save(Product.builder()
                    .productCode("P-014")
                    .name("미샤 립스틱")
                    .description("매트 립스틱")
                    .unitPrice(7000L)
                    .vendor(vendor4)
                    .status(ProductStatus.ACTIVE)
                    .build());

            Product product15 = productRepository.save(Product.builder()
                    .productCode("P-015")
                    .name("토니모리 아이섀도우 팔레트")
                    .description("12색 아이섀도우")
                    .unitPrice(18000L)
                    .vendor(vendor5)
                    .status(ProductStatus.ACTIVE)
                    .build());

            Product product16 = productRepository.save(Product.builder()
                    .productCode("P-016")
                    .name("토니모리 마스카라")
                    .description("볼륨 마스카라")
                    .unitPrice(12000L)
                    .vendor(vendor5)
                    .status(ProductStatus.ACTIVE)
                    .build());

            Product product17 = productRepository.save(Product.builder()
                    .productCode("P-017")
                    .name("토니모리 블러셔")
                    .description("생기발랄 블러셔")
                    .unitPrice(10000L)
                    .vendor(vendor5)
                    .status(ProductStatus.ACTIVE)
                    .build());

            Product product18 = productRepository.save(Product.builder()
                    .productCode("P-018")
                    .name("네이처리퍼블릭 알로에젤")
                    .description("92% 순수 알로에베라 젤")
                    .unitPrice(8000L)
                    .vendor(vendor6)
                    .status(ProductStatus.DISCONTINUED)
                    .build());

            Product product19 = productRepository.save(Product.builder()
                    .productCode("P-019")
                    .name("설화수 퍼펙팅 쿠션")
                    .description("프리미엄 쿠션 파운데이션")
                    .unitPrice(68000L)
                    .vendor(vendor1)
                    .status(ProductStatus.ACTIVE)
                    .build());

            Product product20 = productRepository.save(Product.builder()
                    .productCode("P-020")
                    .name("라네즈 립글로우밤")
                    .description("촉촉한 립밤")
                    .unitPrice(16000L)
                    .vendor(vendor1)
                    .status(ProductStatus.ACTIVE)
                    .build());

            log.info("✓ 상품 20개 생성");

            // 5. 발주 (PurchaseOrder)
            PurchaseOrder po1 = PurchaseOrder.builder()
                    .vendor(vendor1)
                    .orderCode("PO-20250415-001")
                    .status(PurchaseOrderStatus.APPROVED)
                    .totalAmount(5400000L)
                    .orderDate(LocalDateTime.now().minusDays(3))
                    .expectedDate(LocalDateTime.now().plusDays(4))
                    .description("아모레퍼시픽 정기 발주")
                    .products(new ArrayList<>())
                    .build();

            PurchaseOrderProduct po1Item1 = PurchaseOrderProduct.builder()
                    .purchaseOrder(po1)
                    .product(product1)
                    .orderedQuantity(30)
                    .build();

            PurchaseOrderProduct po1Item2 = PurchaseOrderProduct.builder()
                    .purchaseOrder(po1)
                    .product(product2)
                    .orderedQuantity(50)
                    .build();

            PurchaseOrderProduct po1Item3 = PurchaseOrderProduct.builder()
                    .purchaseOrder(po1)
                    .product(product3)
                    .orderedQuantity(15)
                    .build();

            PurchaseOrderProduct po1Item4 = PurchaseOrderProduct.builder()
                    .purchaseOrder(po1)
                    .product(product4)
                    .orderedQuantity(40)
                    .build();

            po1.getProducts().add(po1Item1);
            po1.getProducts().add(po1Item2);
            po1.getProducts().add(po1Item3);
            po1.getProducts().add(po1Item4);
            purchaseOrderRepository.save(po1);

            PurchaseOrder po2 = PurchaseOrder.builder()
                    .vendor(vendor2)
                    .orderCode("PO-20250416-001")
                    .status(PurchaseOrderStatus.APPROVED)
                    .totalAmount(2100000L)
                    .orderDate(LocalDateTime.now().minusDays(2))
                    .expectedDate(LocalDateTime.now().plusDays(5))
                    .description("LG생활건강 신규 발주")
                    .products(new ArrayList<>())
                    .build();

            PurchaseOrderProduct po2Item1 = PurchaseOrderProduct.builder()
                    .purchaseOrder(po2)
                    .product(product5)
                    .orderedQuantity(100)
                    .build();

            PurchaseOrderProduct po2Item2 = PurchaseOrderProduct.builder()
                    .purchaseOrder(po2)
                    .product(product6)
                    .orderedQuantity(40)
                    .build();

            po2.getProducts().add(po2Item1);
            po2.getProducts().add(po2Item2);
            purchaseOrderRepository.save(po2);

            PurchaseOrder po3 = PurchaseOrder.builder()
                    .vendor(vendor3)
                    .orderCode("PO-20250417-001")
                    .status(PurchaseOrderStatus.APPROVED)
                    .totalAmount(3100000L)
                    .orderDate(LocalDateTime.now().minusDays(1))
                    .expectedDate(LocalDateTime.now().plusDays(3))
                    .description("코스맥스 기능성 화장품 발주")
                    .products(new ArrayList<>())
                    .build();

            PurchaseOrderProduct po3Item1 = PurchaseOrderProduct.builder()
                    .purchaseOrder(po3)
                    .product(product9)
                    .orderedQuantity(50)
                    .build();

            PurchaseOrderProduct po3Item2 = PurchaseOrderProduct.builder()
                    .purchaseOrder(po3)
                    .product(product10)
                    .orderedQuantity(30)
                    .build();

            po3.getProducts().add(po3Item1);
            po3.getProducts().add(po3Item2);
            purchaseOrderRepository.save(po3);

            PurchaseOrder po4 = PurchaseOrder.builder()
                    .vendor(vendor4)
                    .orderCode("PO-20250418-001")
                    .status(PurchaseOrderStatus.APPROVED)
                    .totalAmount(1680000L)
                    .orderDate(LocalDateTime.now())
                    .expectedDate(LocalDateTime.now().plusDays(6))
                    .description("미샤 색조 화장품 발주")
                    .products(new ArrayList<>())
                    .build();

            PurchaseOrderProduct po4Item1 = PurchaseOrderProduct.builder()
                    .purchaseOrder(po4)
                    .product(product12)
                    .orderedQuantity(80)
                    .build();

            PurchaseOrderProduct po4Item2 = PurchaseOrderProduct.builder()
                    .purchaseOrder(po4)
                    .product(product13)
                    .orderedQuantity(100)
                    .build();

            po4.getProducts().add(po4Item1);
            po4.getProducts().add(po4Item2);
            purchaseOrderRepository.save(po4);

            PurchaseOrder po5 = PurchaseOrder.builder()
                    .vendor(vendor5)
                    .orderCode("PO-20250419-001")
                    .status(PurchaseOrderStatus.REQUESTED)
                    .totalAmount(1200000L)
                    .orderDate(LocalDateTime.now())
                    .expectedDate(LocalDateTime.now().plusDays(10))
                    .description("토니모리 색조 제품 - 승인 대기")
                    .products(new ArrayList<>())
                    .build();

            PurchaseOrderProduct po5Item1 = PurchaseOrderProduct.builder()
                    .purchaseOrder(po5)
                    .product(product15)
                    .orderedQuantity(40)
                    .build();

            PurchaseOrderProduct po5Item2 = PurchaseOrderProduct.builder()
                    .purchaseOrder(po5)
                    .product(product16)
                    .orderedQuantity(60)
                    .build();

            po5.getProducts().add(po5Item1);
            po5.getProducts().add(po5Item2);
            purchaseOrderRepository.save(po5);

            log.info("✓ 발주 5건 생성");

            // 6. 입고 (Inbound)
            // 입고 1: 아모레퍼시픽 제품 입고 완료
            Inbound inbound1 = Inbound.builder()
                    .inboundCode("IB-20250418-001")
                    .status(InboundStatus.COMPLETED)
                    .managerName(manager1.getName())
                    .workerName(worker1.getName())
                    .scheduledDate(LocalDateTime.now().minusDays(1))
                    .purchaseOrder(po1)
                    .description("아모레퍼시픽 정기 발주 입고 완료")
                    .products(new ArrayList<>())
                    .build();

            InboundProduct inbound1Item1 = InboundProduct.builder()
                    .inbound(inbound1)
                    .product(product1)
                    .purchaseOrderProduct(po1Item1)
                    .receivedQuantity(30)
                    .lotNumber("LOT-2025-AP-001")
                    .locationCode(loc1.getLocationCode())
                    .description("설화수 윤조에센스 정상 입고")
                    .build();

            InboundProduct inbound1Item2 = InboundProduct.builder()
                    .inbound(inbound1)
                    .product(product2)
                    .purchaseOrderProduct(po1Item2)
                    .receivedQuantity(50)
                    .lotNumber("LOT-2025-AP-002")
                    .locationCode(loc1.getLocationCode())
                    .description("라네즈 워터슬리핑마스크 정상 입고")
                    .build();

            InboundProduct inbound1Item3 = InboundProduct.builder()
                    .inbound(inbound1)
                    .product(product3)
                    .purchaseOrderProduct(po1Item3)
                    .receivedQuantity(15)
                    .lotNumber("LOT-2025-AP-003")
                    .locationCode(loc2.getLocationCode())
                    .description("설화수 자음생크림 정상 입고")
                    .build();

            InboundProduct inbound1Item4 = InboundProduct.builder()
                    .inbound(inbound1)
                    .product(product4)
                    .purchaseOrderProduct(po1Item4)
                    .receivedQuantity(40)
                    .lotNumber("LOT-2025-AP-004")
                    .locationCode(loc2.getLocationCode())
                    .description("라네즈 크림스킨 정상 입고")
                    .build();

            inbound1.getProducts().add(inbound1Item1);
            inbound1.getProducts().add(inbound1Item2);
            inbound1.getProducts().add(inbound1Item3);
            inbound1.getProducts().add(inbound1Item4);
            inboundRepository.save(inbound1);

            // 입고 2: 코스맥스 제품 검수중
            Inbound inbound2 = Inbound.builder()
                    .inboundCode("IB-20250419-001")
                    .status(InboundStatus.INSPECTING)
                    .managerName(manager2.getName())
                    .workerName(worker2.getName())
                    .scheduledDate(LocalDateTime.now())
                    .purchaseOrder(po3)
                    .description("기능성 화장품 검수 진행중")
                    .products(new ArrayList<>())
                    .build();

            InboundProduct inbound2Item1 = InboundProduct.builder()
                    .inbound(inbound2)
                    .product(product9)
                    .purchaseOrderProduct(po3Item1)
                    .receivedQuantity(50)
                    .lotNumber("LOT-2025-CSM-001")
                    .locationCode(loc5.getLocationCode())
                    .description("비타민C 세럼 검수중")
                    .build();

            InboundProduct inbound2Item2 = InboundProduct.builder()
                    .inbound(inbound2)
                    .product(product10)
                    .purchaseOrderProduct(po3Item2)
                    .receivedQuantity(30)
                    .lotNumber("LOT-2025-CSM-002")
                    .locationCode(loc5.getLocationCode())
                    .description("레티놀 크림 검수중")
                    .build();

            inbound2.getProducts().add(inbound2Item1);
            inbound2.getProducts().add(inbound2Item2);
            inboundRepository.save(inbound2);

            // 입고 3: 미샤 제품 입고 예정
            Inbound inbound3 = Inbound.builder()
                    .inboundCode("IB-20250420-001")
                    .status(InboundStatus.SCHEDULED)
                    .managerName(manager1.getName())
                    .scheduledDate(LocalDateTime.now().plusDays(5))
                    .purchaseOrder(po4)
                    .description("미샤 쿠션, 비비크림 입고 예정")
                    .products(new ArrayList<>())
                    .build();
            inboundRepository.save(inbound3);

            // 입고 4: LG생활건강 제품 입고 예정
            Inbound inbound4 = Inbound.builder()
                    .inboundCode("IB-20250421-001")
                    .status(InboundStatus.SCHEDULED)
                    .managerName(manager2.getName())
                    .scheduledDate(LocalDateTime.now().plusDays(7))
                    .purchaseOrder(po2)
                    .description("더페이스샵, 빌리프 입고 대기")
                    .products(new ArrayList<>())
                    .build();
            inboundRepository.save(inbound4);

            log.info("✓ 입고 4건 생성");

            // 7. Task 생성
            if (taskRepository.count() == 0) {
                log.info("========== Task 초기 데이터 생성 시작 ==========");

                // 검수 작업 1: 완료됨
                Task task1 = Task.builder()
                        .category(TaskCategory.INSPECTION)
                        .status(TaskStatus.PENDING)
                        .referenceId(inbound1.getId())
                        .build();
                task1.assignTo(worker1);
                task1.start();
                task1.complete("검수 완료. 전량 정상");
                taskRepository.save(task1);

                InspectionTask inspection1 = InspectionTask.builder()
                        .task(task1)
                        .inboundId(inbound1.getId())
                        .build();
                inspectionTaskRepository.save(inspection1);

                // 검수 작업 2: 진행중
                Task task2 = Task.builder()
                        .category(TaskCategory.INSPECTION)
                        .status(TaskStatus.PENDING)
                        .referenceId(inbound2.getId())
                        .build();
                task2.assignTo(worker2);
                task2.start();
                taskRepository.save(task2);

                InspectionTask inspection2 = InspectionTask.builder()
                        .task(task2)
                        .inboundId(inbound2.getId())
                        .build();
                inspectionTaskRepository.save(inspection2);

                // 검수 작업 3: 할당됨
                Task task3 = Task.builder()
                        .category(TaskCategory.INSPECTION)
                        .status(TaskStatus.PENDING)
                        .referenceId(inbound3.getId())
                        .build();
                task3.assignTo(worker1);
                taskRepository.save(task3);

                InspectionTask inspection3 = InspectionTask.builder()
                        .task(task3)
                        .inboundId(inbound3.getId())
                        .build();
                inspectionTaskRepository.save(inspection3);

                // 검수 작업 4: 대기중
                Task task4 = Task.builder()
                        .category(TaskCategory.INSPECTION)
                        .status(TaskStatus.PENDING)
                        .referenceId(inbound4.getId())
                        .build();
                taskRepository.save(task4);

                InspectionTask inspection4 = InspectionTask.builder()
                        .task(task4)
                        .inboundId(inbound4.getId())
                        .build();
                inspectionTaskRepository.save(inspection4);

                log.info("✓ 검수 작업 4건 생성 (완료 1, 진행중 1, 할당 1, 대기 1)");
            }

            // 8. 재고 생성 (입고 완료 기준)
            if (inventoryRepository.count() == 0) {
                log.info("========== 재고(Inventory) 초기 데이터 생성 시작 ==========");

                // 완료된 입고(inbound1)만 처리
                if (inbound1.getStatus() == InboundStatus.COMPLETED) {
                    // Item 1: 설화수 윤조에센스
                    Inventory inv1 = Inventory.builder()
                            .product(product1)
                            .location(loc1)
                            .lotNumber("LOT-2025-AP-001")
                            .quantity(30)
                            .description("입고 완료로 자동 생성된 재고")
                            .build();
                    loc1.increaseUsedCapacity(30);
                    inventoryRepository.save(inv1);

                    // Item 2: 라네즈 워터슬리핑마스크
                    Inventory inv2 = Inventory.builder()
                            .product(product2)
                            .location(loc1)
                            .lotNumber("LOT-2025-AP-002")
                            .quantity(50)
                            .description("입고 완료로 자동 생성된 재고")
                            .build();
                    loc1.increaseUsedCapacity(50);
                    inventoryRepository.save(inv2);

                    // Item 3: 설화수 자음생크림
                    Inventory inv3 = Inventory.builder()
                            .product(product3)
                            .location(loc2)
                            .lotNumber("LOT-2025-AP-003")
                            .quantity(15)
                            .description("입고 완료로 자동 생성된 재고")
                            .build();
                    loc2.increaseUsedCapacity(15);
                    inventoryRepository.save(inv3);

                    // Item 4: 미샤 쿠션
                    Inventory inv4 = Inventory.builder()
                            .product(product12)
                            .location(loc2)
                            .lotNumber("LOT-2025-AP-004")
                            .quantity(40)
                            .description("입고 완료로 자동 생성된 재고")
                            .build();
                    loc2.increaseUsedCapacity(40);
                    inventoryRepository.save(inv4);

                    // Item 5: 설화수 윤조에센스 - 같은 상품 다른 로트, 위치
                    Inventory inv5 = Inventory.builder()
                            .product(product1)
                            .location(loc2)
                            .lotNumber("LOT-2025-AP-002")
                            .quantity(100)
                            .description("입고 완료로 자동 생성된 재고")
                            .build();
                    loc2.increaseUsedCapacity(20);
                    inventoryRepository.save(inv5);
                }

                log.info("✓ 재고 데이터 5건 생성 완료 (입고 완료 기준)");
            }

            log.info("========== 초기 데이터 생성 완료 ==========");
            log.info("");
            log.info("로그인 계정:");
            log.info("  - admin@company.com / admin1234 (관리자)");
            log.info("  - manager1@company.com / manager1234 (매니저)");
            log.info("  - worker1@company.com / worker1234 (작업자)");
            log.info("");
            log.info("생성된 데이터:");
            log.info("  - 공급업체: 6개 (아모레퍼시픽, LG생활건강, 코스맥스 등)");
            log.info("  - 직원: 7명 (관리자 1, 매니저 2, 작업자 4)");
            log.info("  - 로케이션: 8개 (01-A ~ 06-A)");
            log.info("  - 상품: 20개 (설화수, 라네즈, 미샤, 토니모리 등 화장품)");
            log.info("  - 발주: 5건 (승인 4, 대기 1)");
            log.info("  - 입고: 4건 (완료 1, 검수중 1, 예정 2)");
            log.info("  - 검수 작업: 4건 (완료 1, 진행중 1, 할당 1, 대기 1)");
            log.info("  - 재고: 5건 (완료된 입고 기준)");
            log.info("===========================================");
        };
    }
}