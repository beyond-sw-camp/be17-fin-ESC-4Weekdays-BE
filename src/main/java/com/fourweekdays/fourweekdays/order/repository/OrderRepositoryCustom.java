package com.fourweekdays.fourweekdays.order.repository;

import com.fourweekdays.fourweekdays.order.model.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrderRepositoryCustom {

    Page<Order> findAllWithPaging(Pageable pageable);
}
