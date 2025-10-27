package com.fourweekdays.fourweekdays.order.service;

import com.fourweekdays.fourweekdays.order.exception.OrderException;
import com.fourweekdays.fourweekdays.order.model.dto.response.OrderReadDto;
import com.fourweekdays.fourweekdays.order.model.entity.Order;
import com.fourweekdays.fourweekdays.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static com.fourweekdays.fourweekdays.order.exception.OrderExceptionType.ORDER_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;

    public OrderReadDto readd(Long id) {
        Order result = orderRepository.findById(id).orElseThrow(() -> new OrderException(ORDER_NOT_FOUND));
        return OrderReadDto.from(result);
    }
}
