package com.fourweekdays.fourweekdays.outbound.repository;

import com.fourweekdays.fourweekdays.order.model.entity.Order;
import com.fourweekdays.fourweekdays.outbound.model.entity.Outbound;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OutboundRepository extends JpaRepository<Outbound, Long> {
    boolean existsByOrder(Order order);
}
