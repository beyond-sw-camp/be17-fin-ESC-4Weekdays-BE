package com.fourweekdays.fourweekdays.order.repository;

import com.fourweekdays.fourweekdays.order.model.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OrderRepository extends OrderRepositoryCustom, JpaRepository<Order, Long> {
    Optional<Order> findByOrderCode(String orderCode);
}
