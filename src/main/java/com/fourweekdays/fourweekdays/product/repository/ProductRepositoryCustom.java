package com.fourweekdays.fourweekdays.product.repository;

import com.fourweekdays.fourweekdays.product.model.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductRepositoryCustom {

    Page<Product> findAllWithPaging(Pageable pageable);
}
