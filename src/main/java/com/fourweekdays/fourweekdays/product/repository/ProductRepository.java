package com.fourweekdays.fourweekdays.product.repository;

import com.fourweekdays.fourweekdays.product.model.entity.Product;
import com.fourweekdays.fourweekdays.product.model.entity.ProductStatus;
import com.fourweekdays.fourweekdays.vendor.model.entity.Vendor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long>, ProductRepositoryCustom {

    boolean existsByVendorAndName(Vendor vendor, String name);

    // ✅ ACTIVE 상태 필터용
    Page<Product> findByStatus(ProductStatus status, Pageable pageable);

    // ✅ 특정 공급업체 + ACTIVE 상태 필터용
    Page<Product> findByVendorIdAndStatus(Long vendorId, ProductStatus status, Pageable pageable);
}
