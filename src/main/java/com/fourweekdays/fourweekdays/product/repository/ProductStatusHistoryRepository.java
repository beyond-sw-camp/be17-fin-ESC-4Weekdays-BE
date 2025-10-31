package com.fourweekdays.fourweekdays.product.repository;

import com.fourweekdays.fourweekdays.product.model.entity.ProductStatusHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductStatusHistoryRepository extends JpaRepository<ProductStatusHistory, Long> {
    List<ProductStatusHistory> findByProductIdOrderByCreatedAtDesc(Long productId);
}
