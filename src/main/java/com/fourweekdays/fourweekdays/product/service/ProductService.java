package com.fourweekdays.fourweekdays.product.service;

import com.fourweekdays.fourweekdays.common.generator.CodeGenerator;
import com.fourweekdays.fourweekdays.product.exception.ProductException;
import com.fourweekdays.fourweekdays.product.model.dto.request.ProductCreateDto;
import com.fourweekdays.fourweekdays.product.model.dto.request.ProductSearchRequest;
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

    public Page<ProductReadDto> searchProduct(ProductSearchRequest request, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Product> products = productRepository.searchProducts(pageable, request);

        return products.map(ProductReadDto::from);
    }

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
}
