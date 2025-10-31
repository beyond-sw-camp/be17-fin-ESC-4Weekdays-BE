package com.fourweekdays.fourweekdays.outbound.repository;

import com.fourweekdays.fourweekdays.order.model.entity.Order;
import com.fourweekdays.fourweekdays.outbound.model.entity.Outbound;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OutboundRepository extends JpaRepository<Outbound, Long> {
    boolean existsByOrder(Order order);
}
