package com.fourweekdays.fourweekdays.product.repository;

import com.fourweekdays.fourweekdays.product.model.entity.Product;
import com.fourweekdays.fourweekdays.product.model.entity.ProductStatus;
import com.fourweekdays.fourweekdays.vendor.model.entity.Vendor;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long>, ProductRepositoryCustom {

    boolean existsByVendorAndName(Vendor vendor, String name);

    // ACTIVE 상태 필터용
    Page<Product> findByStatus(ProductStatus status, Pageable pageable);

    // 특정 공급업체 + ACTIVE 상태 필터용
    Page<Product> findByVendorIdAndStatus(Long vendorId, ProductStatus status, Pageable pageable);

    // 상품코드로 조회하면서 PESSIMISTIC_WRITE 락을 적용
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Product> findByProductCode(String productCode);

    // 특정 공급업체와 상태로 필터링한 상품을 PESSIMISTIC_WRITE 락을 적용하여 조회
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    List<Product> findByVendorAndStatus(Vendor vendor, ProductStatus status);
}
