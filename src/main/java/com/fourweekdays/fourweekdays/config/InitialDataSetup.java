package com.fourweekdays.fourweekdays.config;

import com.fourweekdays.fourweekdays.common.vo.Address;
import com.fourweekdays.fourweekdays.inbound.model.entity.Inbound;
import com.fourweekdays.fourweekdays.inbound.model.entity.InboundProduct;
import com.fourweekdays.fourweekdays.inbound.model.entity.InboundStatus;
import com.fourweekdays.fourweekdays.inbound.repository.InboundRepository;
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

    @Bean
    CommandLineRunner initDatabase(
            VendorRepository vendorRepository,
            MemberRepository memberRepository,
            ProductRepository productRepository,
            PurchaseOrderRepository purchaseOrderRepository,
            InboundRepository inboundRepository,
            PasswordEncoder passwordEncoder,
            TaskRepository taskRepository,
            InspectionTaskRepository inspectionTaskRepository
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
                    .joinAt(LocalDateTime.now().minusYears(1))
                    .build());

            Member worker2 = memberRepository.save(Member.builder()
                    .email("worker2@company.com")
                    .password(passwordEncoder.encode("worker1234"))
                    .name("최작업자")
                    .phoneNumber("010-3334-4444")
                    .role(MemberRole.WORKER)
                    .status(AuthStatus.ACTIVE)
                    .joinAt(LocalDateTime.now().minusMonths(8))
                    .build());

            Member worker3 = memberRepository.save(Member.builder()
                    .email("worker3@company.com")
                    .password(passwordEncoder.encode("worker1234"))
                    .name("정작업자")
                    .phoneNumber("010-3335-5555")
                    .role(MemberRole.WORKER)
                    .status(AuthStatus.ACTIVE)
                    .joinAt(LocalDateTime.now().minusMonths(6))
                    .build());

            Member worker4 = memberRepository.save(Member.builder()
                    .email("worker4@company.com")
                    .password(passwordEncoder.encode("worker1234"))
                    .name("강작업자")
                    .phoneNumber("010-3336-6666")
                    .role(MemberRole.WORKER)
                    .status(AuthStatus.ACTIVE)
                    .joinAt(LocalDateTime.now().minusMonths(3))
                    .build());

            log.info("✓ 직원 7명 생성");

            // 3. 상품 (화장품)
            // 아모레퍼시픽 제품들
            Product product1 = Product.builder()
                    .productCode("PRD-001")
                    .name("설화수 자음생 크림")
                    .unit("EA")
                    .unitPrice(380000L)
                    .description("프리미엄 안티에이징 크림 60ml")
                    .status(ProductStatus.ACTIVE)
                    .build();
            product1.mappingVendor(vendor1);
            productRepository.save(product1);

            Product product2 = Product.builder()
                    .productCode("PRD-002")
                    .name("라네즈 워터뱅크 세럼")
                    .unit("EA")
                    .unitPrice(32000L)
                    .description("수분 보습 세럼 70ml")
                    .status(ProductStatus.ACTIVE)
                    .build();
            product2.mappingVendor(vendor1);
            productRepository.save(product2);

            Product product3 = Product.builder()
                    .productCode("PRD-003")
                    .name("헤라 센슈얼 누드 립스틱")
                    .unit("EA")
                    .unitPrice(28000L)
                    .description("인기 색조 립스틱")
                    .status(ProductStatus.ACTIVE)
                    .build();
            product3.mappingVendor(vendor1);
            productRepository.save(product3);

            Product product4 = Product.builder()
                    .productCode("PRD-004")
                    .name("이니스프리 그린티 토너")
                    .unit("EA")
                    .unitPrice(15000L)
                    .description("제주 녹차 토너 200ml")
                    .status(ProductStatus.ACTIVE)
                    .build();
            product4.mappingVendor(vendor1);
            productRepository.save(product4);

            // LG생활건강 제품들
            Product product5 = Product.builder()
                    .productCode("PRD-005")
                    .name("더페이스샵 클렌징 폼")
                    .unit("EA")
                    .unitPrice(8000L)
                    .description("라이스 워터 클렌징폼 150ml")
                    .status(ProductStatus.ACTIVE)
                    .build();
            product5.mappingVendor(vendor2);
            productRepository.save(product5);

            Product product6 = Product.builder()
                    .productCode("PRD-006")
                    .name("빌리프 아쿠아밤")
                    .unit("EA")
                    .unitPrice(38000L)
                    .description("수분 크림 75ml")
                    .status(ProductStatus.ACTIVE)
                    .build();
            product6.mappingVendor(vendor2);
            productRepository.save(product6);

            Product product7 = Product.builder()
                    .productCode("PRD-007")
                    .name("숨 시크릿 에센스")
                    .unit("EA")
                    .unitPrice(45000L)
                    .description("발효 에센스 50ml")
                    .status(ProductStatus.ACTIVE)
                    .build();
            product7.mappingVendor(vendor2);
            productRepository.save(product7);

            Product product8 = Product.builder()
                    .productCode("PRD-008")
                    .name("오휘 프라임 어드바이저")
                    .unit("EA")
                    .unitPrice(65000L)
                    .description("안티에이징 앰플 40ml")
                    .status(ProductStatus.ACTIVE)
                    .build();
            product8.mappingVendor(vendor2);
            productRepository.save(product8);

            // 코스맥스 제품들
            Product product9 = Product.builder()
                    .productCode("PRD-009")
                    .name("비타민C 세럼")
                    .unit("EA")
                    .unitPrice(25000L)
                    .description("ODM 제조 비타민 세럼 30ml")
                    .status(ProductStatus.ACTIVE)
                    .build();
            product9.mappingVendor(vendor3);
            productRepository.save(product9);

            Product product10 = Product.builder()
                    .productCode("PRD-010")
                    .name("레티놀 크림")
                    .unit("EA")
                    .unitPrice(35000L)
                    .description("주름개선 기능성 크림 50ml")
                    .status(ProductStatus.ACTIVE)
                    .build();
            product10.mappingVendor(vendor3);
            productRepository.save(product10);

            Product product11 = Product.builder()
                    .productCode("PRD-011")
                    .name("히알루론산 토너")
                    .unit("EA")
                    .unitPrice(18000L)
                    .description("고보습 토너 200ml")
                    .status(ProductStatus.ACTIVE)
                    .build();
            product11.mappingVendor(vendor3);
            productRepository.save(product11);

            // 에이블씨엔씨 제품들
            Product product12 = Product.builder()
                    .productCode("PRD-012")
                    .name("미샤 쿠션 파운데이션")
                    .unit("EA")
                    .unitPrice(14000L)
                    .description("매직 쿠션 커버 15g")
                    .status(ProductStatus.ACTIVE)
                    .build();
            product12.mappingVendor(vendor4);
            productRepository.save(product12);

            Product product13 = Product.builder()
                    .productCode("PRD-013")
                    .name("미샤 퍼펙트 커버 비비크림")
                    .unit("EA")
                    .unitPrice(9800L)
                    .description("올인원 비비크림 50ml")
                    .status(ProductStatus.ACTIVE)
                    .build();
            product13.mappingVendor(vendor4);
            productRepository.save(product13);

            Product product14 = Product.builder()
                    .productCode("PRD-014")
                    .name("미샤 타임 레볼루션 에센스")
                    .unit("EA")
                    .unitPrice(35000L)
                    .description("발효 에센스 150ml")
                    .status(ProductStatus.ACTIVE)
                    .build();
            product14.mappingVendor(vendor4);
            productRepository.save(product14);

            // 토니모리 제품들
            Product product15 = Product.builder()
                    .productCode("PRD-015")
                    .name("토니모리 치크 블러셔")
                    .unit("EA")
                    .unitPrice(12000L)
                    .description("생기 블러셔 4.5g")
                    .status(ProductStatus.ACTIVE)
                    .build();
            product15.mappingVendor(vendor5);
            productRepository.save(product15);

            Product product16 = Product.builder()
                    .productCode("PRD-016")
                    .name("토니모리 립틴트")
                    .unit("EA")
                    .unitPrice(9900L)
                    .description("데일리 립틴트 8ml")
                    .status(ProductStatus.ACTIVE)
                    .build();
            product16.mappingVendor(vendor5);
            productRepository.save(product16);

            Product product17 = Product.builder()
                    .productCode("PRD-017")
                    .name("토니모리 마스카라")
                    .unit("EA")
                    .unitPrice(15000L)
                    .description("롱래쉬 마스카라 10ml")
                    .status(ProductStatus.ACTIVE)
                    .build();
            product17.mappingVendor(vendor5);
            productRepository.save(product17);

            // 네이처리퍼블릭 제품들
            Product product18 = Product.builder()
                    .productCode("PRD-018")
                    .name("알로에 수딩젤")
                    .unit("EA")
                    .unitPrice(7900L)
                    .description("알로에베라 92% 진정젤 300ml")
                    .status(ProductStatus.ACTIVE)
                    .build();
            product18.mappingVendor(vendor6);
            productRepository.save(product18);

            Product product19 = Product.builder()
                    .productCode("PRD-019")
                    .name("아르간 에센셜 오일")
                    .unit("EA")
                    .unitPrice(25000L)
                    .description("모로코 아르간 오일 30ml")
                    .status(ProductStatus.INACTIVE)
                    .build();
            product19.mappingVendor(vendor6);
            productRepository.save(product19);

            Product product20 = Product.builder()
                    .productCode("PRD-020")
                    .name("시카 크림")
                    .unit("EA")
                    .unitPrice(22000L)
                    .description("병풀추출물 진정크림 50ml")
                    .status(ProductStatus.ACTIVE)
                    .build();
            product20.mappingVendor(vendor6);
            productRepository.save(product20);

            log.info("✓ 상품 20개 생성");

            // 4. 발주
            // 발주 1: 아모레퍼시픽 제품 대량 발주
            PurchaseOrder po1 = PurchaseOrder.builder()
                    .orderCode("PO-20250415-001")
                    .vendor(vendor1)
                    .orderDate(LocalDateTime.now().minusDays(15))
                    .expectedDate(LocalDateTime.now().plusDays(3))
                    .status(PurchaseOrderStatus.APPROVED)
                    .description("설화수, 라네즈 정기 발주")
                    .products(new ArrayList<>())
                    .build();

            PurchaseOrderProduct po1Item1 = PurchaseOrderProduct.builder()
                    .purchaseOrder(po1)
                    .product(product1)  // 설화수 크림
                    .orderedQuantity(5)
                    .description("VIP 고객용")
                    .build();

            PurchaseOrderProduct po1Item2 = PurchaseOrderProduct.builder()
                    .purchaseOrder(po1)
                    .product(product2)  // 라네즈 세럼
                    .orderedQuantity(20)
                    .description("베스트셀러 재고 보충")
                    .build();

            PurchaseOrderProduct po1Item3 = PurchaseOrderProduct.builder()
                    .purchaseOrder(po1)
                    .product(product3)  // 헤라 립스틱
                    .orderedQuantity(30)
                    .description("시즌 신제품")
                    .build();

            PurchaseOrderProduct po1Item4 = PurchaseOrderProduct.builder()
                    .purchaseOrder(po1)
                    .product(product4)  // 이니스프리 토너
                    .orderedQuantity(50)
                    .description("인기 상품")
                    .build();

            po1.getProducts().add(po1Item1);
            po1.getProducts().add(po1Item2);
            po1.getProducts().add(po1Item3);
            po1.getProducts().add(po1Item4);
            purchaseOrderRepository.save(po1);

            // 발주 2: LG생활건강 제품 발주
            PurchaseOrder po2 = PurchaseOrder.builder()
                    .orderCode("PO-20250416-001")
                    .vendor(vendor2)
                    .orderDate(LocalDateTime.now().minusDays(10))
                    .expectedDate(LocalDateTime.now().plusDays(7))
                    .status(PurchaseOrderStatus.REQUESTED)
                    .description("더페이스샵, 빌리프 신규 입고")
                    .products(new ArrayList<>())
                    .build();

            PurchaseOrderProduct po2Item1 = PurchaseOrderProduct.builder()
                    .purchaseOrder(po2)
                    .product(product5)  // 클렌징 폼
                    .orderedQuantity(100)
                    .description("기초 클렌징")
                    .build();

            PurchaseOrderProduct po2Item2 = PurchaseOrderProduct.builder()
                    .purchaseOrder(po2)
                    .product(product6)  // 빌리프 아쿠아밤
                    .orderedQuantity(30)
                    .description("프리미엄 라인")
                    .build();

            PurchaseOrderProduct po2Item3 = PurchaseOrderProduct.builder()
                    .purchaseOrder(po2)
                    .product(product7)  // 숨 에센스
                    .orderedQuantity(20)
                    .description("고급 라인")
                    .build();

            po2.getProducts().add(po2Item1);
            po2.getProducts().add(po2Item2);
            po2.getProducts().add(po2Item3);
            purchaseOrderRepository.save(po2);

            // 발주 3: 코스맥스 ODM 제품 발주
            PurchaseOrder po3 = PurchaseOrder.builder()
                    .orderCode("PO-20250417-001")
                    .vendor(vendor3)
                    .orderDate(LocalDateTime.now().minusDays(8))
                    .expectedDate(LocalDateTime.now().plusDays(10))
                    .status(PurchaseOrderStatus.APPROVED)
                    .description("기능성 화장품 긴급 발주")
                    .products(new ArrayList<>())
                    .build();

            PurchaseOrderProduct po3Item1 = PurchaseOrderProduct.builder()
                    .purchaseOrder(po3)
                    .product(product9)  // 비타민C 세럼
                    .orderedQuantity(40)
                    .description("인기 기능성")
                    .build();

            PurchaseOrderProduct po3Item2 = PurchaseOrderProduct.builder()
                    .purchaseOrder(po3)
                    .product(product10)  // 레티놀 크림
                    .orderedQuantity(25)
                    .description("주름개선")
                    .build();

            po3.getProducts().add(po3Item1);
            po3.getProducts().add(po3Item2);
            purchaseOrderRepository.save(po3);

            // 발주 4: 미샤 제품 발주
            PurchaseOrder po4 = PurchaseOrder.builder()
                    .orderCode("PO-20250418-001")
                    .vendor(vendor4)
                    .orderDate(LocalDateTime.now().minusDays(5))
                    .expectedDate(LocalDateTime.now().plusDays(5))
                    .status(PurchaseOrderStatus.APPROVED)
                    .description("미샤 인기 제품 재입고")
                    .products(new ArrayList<>())
                    .build();

            PurchaseOrderProduct po4Item1 = PurchaseOrderProduct.builder()
                    .purchaseOrder(po4)
                    .product(product12)  // 쿠션
                    .orderedQuantity(80)
                    .description("베스트 쿠션")
                    .build();

            PurchaseOrderProduct po4Item2 = PurchaseOrderProduct.builder()
                    .purchaseOrder(po4)
                    .product(product13)  // 비비크림
                    .orderedQuantity(60)
                    .description("스테디셀러")
                    .build();

            PurchaseOrderProduct po4Item3 = PurchaseOrderProduct.builder()
                    .purchaseOrder(po4)
                    .product(product14)  // 에센스
                    .orderedQuantity(15)
                    .description("고가 라인")
                    .build();

            po4.getProducts().add(po4Item1);
            po4.getProducts().add(po4Item2);
            po4.getProducts().add(po4Item3);
            purchaseOrderRepository.save(po4);

            // 발주 5: 토니모리 색조 제품 발주
            PurchaseOrder po5 = PurchaseOrder.builder()
                    .orderCode("PO-20250419-001")
                    .vendor(vendor5)
                    .orderDate(LocalDateTime.now().minusDays(3))
                    .expectedDate(LocalDateTime.now().plusDays(12))
                    .status(PurchaseOrderStatus.REQUESTED)
                    .description("색조 화장품 대량 발주")
                    .products(new ArrayList<>())
                    .build();

            PurchaseOrderProduct po5Item1 = PurchaseOrderProduct.builder()
                    .purchaseOrder(po5)
                    .product(product15)  // 블러셔
                    .orderedQuantity(50)
                    .description("봄 시즌 색조")
                    .build();

            PurchaseOrderProduct po5Item2 = PurchaseOrderProduct.builder()
                    .purchaseOrder(po5)
                    .product(product16)  // 립틴트
                    .orderedQuantity(70)
                    .description("인기 틴트")
                    .build();

            PurchaseOrderProduct po5Item3 = PurchaseOrderProduct.builder()
                    .purchaseOrder(po5)
                    .product(product17)  // 마스카라
                    .orderedQuantity(45)
                    .description("롱래쉬")
                    .build();

            po5.getProducts().add(po5Item1);
            po5.getProducts().add(po5Item2);
            po5.getProducts().add(po5Item3);
            purchaseOrderRepository.save(po5);

            log.info("✓ 발주 5건 생성");

            // 5. 입고
            // 입고 1: 아모레퍼시픽 제품 완료된 입고
            Inbound inbound1 = Inbound.builder()
                    .inboundCode("IB-20250418-001")
                    .status(InboundStatus.COMPLETED)
                    .managerName(manager1.getName())
                    .workerName(worker1.getName())
                    .scheduledDate(LocalDateTime.now().minusDays(2))
                    .purchaseOrder(po1)
                    .description("설화수, 라네즈 정상 입고 완료")
                    .products(new ArrayList<>())
                    .build();

            InboundProduct inbound1Item1 = InboundProduct.builder()
                    .inbound(inbound1)
                    .product(product1)
                    .purchaseOrderProduct(po1Item1)
                    .receivedQuantity(5)
                    .lotNumber("LOT-2025-APF-001")
                    .locationCode("A-01-01")
                    .description("프리미엄존 보관")
                    .build();

            InboundProduct inbound1Item2 = InboundProduct.builder()
                    .inbound(inbound1)
                    .product(product2)
                    .purchaseOrderProduct(po1Item2)
                    .receivedQuantity(20)
                    .lotNumber("LOT-2025-APF-002")
                    .locationCode("A-01-02")
                    .description("스킨케어존")
                    .build();

            InboundProduct inbound1Item3 = InboundProduct.builder()
                    .inbound(inbound1)
                    .product(product3)
                    .purchaseOrderProduct(po1Item3)
                    .receivedQuantity(30)
                    .lotNumber("LOT-2025-APF-003")
                    .locationCode("A-02-01")
                    .description("색조 화장품존")
                    .build();

            InboundProduct inbound1Item4 = InboundProduct.builder()
                    .inbound(inbound1)
                    .product(product4)
                    .purchaseOrderProduct(po1Item4)
                    .receivedQuantity(50)
                    .lotNumber("LOT-2025-APF-004")
                    .locationCode("A-01-03")
                    .description("대용량 토너")
                    .build();

            inbound1.getProducts().add(inbound1Item1);
            inbound1.getProducts().add(inbound1Item2);
            inbound1.getProducts().add(inbound1Item3);
            inbound1.getProducts().add(inbound1Item4);
            inboundRepository.save(inbound1);

            // 입고 2: 코스맥스 제품 입고중
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
                    .receivedQuantity(40)
                    .lotNumber("LOT-2025-CSM-001")
                    .locationCode("B-01-01")
                    .description("비타민C 세럼 검수중")
                    .build();

            InboundProduct inbound2Item2 = InboundProduct.builder()
                    .inbound(inbound2)
                    .product(product10)
                    .purchaseOrderProduct(po3Item2)
                    .receivedQuantity(25)
                    .lotNumber("LOT-2025-CSM-002")
                    .locationCode("B-01-02")
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

            if (taskRepository.count() > 0) {
                log.info("Task 데이터가 이미 존재합니다.");
                return;
            }

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
                    .referenceId(inbound2.getId())
                    .build();
            taskRepository.save(task4);

            InspectionTask inspection4 = InspectionTask.builder()
                    .task(task4)
                    .inboundId(inbound2.getId())
                    .build();
            inspectionTaskRepository.save(inspection4);

            log.info("✓ 검수 작업 4건 생성 (완료 1, 진행중 1, 할당 1, 대기 1)");
            log.info("========== Task 초기 데이터 생성 완료 ==========");

            log.info("✓ 입고 4건 생성");
            log.info("========== 초기 데이터 생성 완료 ==========");
            log.info("로그인 계정:");
            log.info("  - admin@company.com / admin1234 (관리자)");
            log.info("  - manager1@company.com / manager1234 (매니저)");
            log.info("  - worker1@company.com / worker1234 (작업자)");
            log.info("");
            log.info("생성된 데이터:");
            log.info("  - 공급업체: 6개 (아모레퍼시픽, LG생활건강, 코스맥스 등)");
            log.info("  - 직원: 7명 (관리자 1, 매니저 2, 작업자 4)");
            log.info("  - 상품: 20개 (설화수, 라네즈, 미샤, 토니모리 등 화장품)");
            log.info("  - 발주: 5건");
            log.info("  - 입고: 4건 (완료 1, 진행중 1, 예정 2)");
            log.info("===========================================");
        };
    }
}