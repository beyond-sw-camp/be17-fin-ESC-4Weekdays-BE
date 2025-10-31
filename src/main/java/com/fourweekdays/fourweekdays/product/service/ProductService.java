package com.fourweekdays.fourweekdays.product.service;

import com.fourweekdays.fourweekdays.common.generator.CodeGenerator;
import com.fourweekdays.fourweekdays.product.exception.ProductException;
import com.fourweekdays.fourweekdays.product.model.dto.request.ProductCreateDto;
import com.fourweekdays.fourweekdays.product.model.dto.request.ProductUpdateDto;
import com.fourweekdays.fourweekdays.product.model.dto.response.ProductReadDto;
import com.fourweekdays.fourweekdays.product.model.entity.Product;
import com.fourweekdays.fourweekdays.product.model.entity.ProductStatus;
import com.fourweekdays.fourweekdays.product.repository.ProductRepository;
import com.fourweekdays.fourweekdays.vendor.exception.VendorException;
import com.fourweekdays.fourweekdays.vendor.model.entity.Vendor;
import com.fourweekdays.fourweekdays.vendor.repository.VendorRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import static com.fourweekdays.fourweekdays.product.exception.ProductExceptionType.PRODUCT_DUPLICATION;
import static com.fourweekdays.fourweekdays.product.exception.ProductExceptionType.PRODUCT_NOT_FOUND;
import static com.fourweekdays.fourweekdays.vendor.exception.VendorExceptionType.VENDOR_NOT_FOUND;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductService {

    private static final String PRODUCT_CODE_PREFIX = "PRD";

    private final ProductRepository productRepository;
    private final VendorRepository vendorRepository;
    private final CodeGenerator codeGenerator;
//    private final CategoryRepository categoryRepository;
//    private final ProductStatusHistoryRepository historyRepository;

    @Transactional
    public Long createProduct(ProductCreateDto requestDto) {
        Vendor vendor = vendorRepository.findById(requestDto.getVendorId())
                .orElseThrow(() -> new VendorException(VENDOR_NOT_FOUND));

        if (productRepository.existsByVendorAndName(vendor, requestDto.getName())) {
            throw new ProductException(PRODUCT_DUPLICATION);
        }

        Product product = requestDto.toEntity(codeGenerator.generate(PRODUCT_CODE_PREFIX));
        product.mappingVendor(vendor); // 연관 관계 매핑 
        return productRepository.save(product).getId();
    }

    public ProductReadDto getProductDetail(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductException(PRODUCT_NOT_FOUND));

        return ProductReadDto.from(product);
    }

//    public Page<ProductReadDto> getProductList(int page, int size) {
//        Pageable pageable = PageRequest.of(page, size);
//        Page<Product> productList = productRepository.findAllWithPaging(pageable);
//        return productList.map(ProductReadDto::from);
//    }

    @Transactional
    public Long update(Long id, ProductUpdateDto requestDto) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductException(PRODUCT_NOT_FOUND));

        vendorRepository.findById(requestDto.getVendorId())
                .orElseThrow(() -> new VendorException(VENDOR_NOT_FOUND));

        product.update(requestDto.getName(), requestDto.getUnit(),
                requestDto.getUnitPrice(), requestDto.getDescription(),
                requestDto.getStatus(), product.getVendor()
        );

        return id;
    }

    @Transactional
    public void delete(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductException(PRODUCT_NOT_FOUND));
        product.delete();
    }

    public Page<ProductReadDto> getProductList(int page, int size) {
        // TODO: 리팩토링 포인트 - HttpRequest가 Service 레이어까지 침범
        Pageable pageable = PageRequest.of(page, size);

        // RequestParam으로 전달된 vendorId 추출
        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attrs != null ? attrs.getRequest() : null;

        Long vendorId = null;
        if (request != null && request.getParameter("vendorId") != null) {
            vendorId = Long.valueOf(request.getParameter("vendorId"));
        }

        Page<Product> productList;

        // vendorId가 존재하면 해당 벤더의 ACTIVE 상품만 조회
        if (vendorId != null) {
            productList = productRepository.findByVendorIdAndStatus(vendorId, ProductStatus.ACTIVE, pageable);
        }
        // vendorId 없으면 전체 ACTIVE 상품만 조회
        else {
            productList = productRepository.findByStatus(ProductStatus.ACTIVE, pageable);
        }

        return productList.map(ProductReadDto::from);
    }


//    @Transactional
//    public Long register(ProductCreateDto dto) {
//        Category category = null;
//
//        // categoryId 존재 시 → 기존 카테고리 연결
//        if (dto.getCategoryId() != null) {
//            category = categoryRepository.findById(dto.getCategoryId())
//                    .orElseThrow(() -> new RuntimeException("해당 카테고리를 찾을 수 없습니다."));
//        }
//
//        // categoryId 없고 이름 정보(categoryLarge~Small)만 있을 경우 → 카테고리 생성 or 조회
//        else if (dto.getCategoryLarge() != null && dto.getCategoryMedium() != null && dto.getCategorySmall() != null) {
//            category = findOrCreateCategory(dto.getCategoryLarge(), dto.getCategoryMedium(), dto.getCategorySmall());
//        }
//
//        // 상품 생성 및 저장
//        Product product = dto.toEntity(category);
//        product.updateStatus(ProductStatus.RECEIVED); // 기본 상태: 입고(RECEIVED)
//        Product saved = productRepository.save(product);
//
//        // 최초 상태 이력 기록
//        ProductStatusHistory history = ProductStatusHistory.builder()
//                .product(saved)
//                .oldStatus(null)
//                .newStatus(ProductStatus.RECEIVED)
//                .changedBy("SYSTEM") // 작업자 ID or 이름 등 정보 필요
//                .build();
//
//        historyRepository.save(history);
//
//        return saved.getId();
//    }

//    // 상태 변경(변경시 ProductStatusHistory에 기록)
//    // 상태 전이 규칙 검증
//    @Transactional
//    public ProductStatusHistory updateProductStatus(Long productId, ProductStatusUpdateDto dto) {
//        Product product = productRepository.findById(productId)
//                .orElseThrow(() -> new RuntimeException("상품을 찾을 수 없습니다."));
//
//        ProductStatus oldStatus = product.getStatus();
//        ProductStatus newStatus = dto.getStatus();
//
//        validateStatusTransition(oldStatus, newStatus);
//        product.updateStatus(newStatus);
//
//        ProductStatusHistory history = ProductStatusHistory.builder()
//                .product(product)
//                .oldStatus(oldStatus)
//                .newStatus(newStatus)
//                .changedBy(dto.getChangedBy())
//                .build();
//
//        return historyRepository.save(history);
//    }

//    // 상태 전이 검증 로직
//    private void validateStatusTransition(ProductStatus oldStatus, ProductStatus newStatus) {
//        if (oldStatus == ProductStatus.APPROVED) {
//            throw new IllegalStateException("승인된 상품은 더 이상 상태를 변경할 수 없습니다.");
//        }
//        if (oldStatus == ProductStatus.RECEIVED && newStatus != ProductStatus.INSPECTING) {
//            throw new IllegalStateException("입고 상태에서는 검수중으로만 이동할 수 있습니다.");
//        }
//        if (oldStatus == ProductStatus.INSPECTING && newStatus != ProductStatus.INSPECTED) {
//            throw new IllegalStateException("검수중 상태에서는 검수 완료로만 이동할 수 있습니다.");
//        }
//        if (oldStatus == ProductStatus.INSPECTED && newStatus != ProductStatus.STORING) {
//            throw new IllegalStateException("검수 완료 상태에서는 적치중으로만 이동할 수 있습니다.");
//        }
//        if (oldStatus == ProductStatus.STORING && newStatus != ProductStatus.APPROVED) {
//            throw new IllegalStateException("적치중 상태에서는 승인으로만 이동할 수 있습니다.");
//        }
//    }

//    // 카테고리 존재하지 않으면 생성
//    private Category findOrCreateCategory(String large, String medium, String small) {
//        Category largeCategory = categoryRepository.findByNameAndParentIsNull(large)
//                .orElseGet(() -> categoryRepository.save(
//                        Category.builder().name(large).active(true).build()
//                ));
//
//        Category mediumCategory = categoryRepository.findByNameAndParent(medium, largeCategory)
//                .orElseGet(() -> categoryRepository.save(
//                        Category.builder().name(medium).parent(largeCategory).active(true).build()
//                ));
//
//        Category smallCategory = categoryRepository.findByNameAndParent(small, mediumCategory)
//                .orElseGet(() -> categoryRepository.save(
//                        Category.builder().name(small).parent(mediumCategory).active(true).build()
//                ));
//
//        return smallCategory;
//    }

}
