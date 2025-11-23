package com.fourweekdays.fourweekdays.order.service;

import com.fourweekdays.fourweekdays.order.exception.OrderException;
import com.fourweekdays.fourweekdays.order.model.dto.response.OrderReadDto;
import com.fourweekdays.fourweekdays.order.model.entity.Order;
import com.fourweekdays.fourweekdays.order.model.entity.OrderStatus;
import com.fourweekdays.fourweekdays.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.fourweekdays.fourweekdays.order.exception.OrderExceptionType.*;

@Service
@RequiredArgsConstructor
public class OrderAdminService {

    private final OrderRepository orderRepository;

    public OrderReadDto orderDetail(Long id) {
        Order result = orderRepository.findById(id).orElseThrow(() -> new OrderException(ORDER_NOT_FOUND));
        return OrderReadDto.from(result);
    }

    public Page<OrderReadDto> orderList(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Order> pageList = orderRepository.findAllWithPaging(pageable);
        return pageList.map(OrderReadDto::from);
    }

    @Transactional
    public void rejectOrder(Long id) {
        Order result = orderRepository.findById(id)
                .orElseThrow(() -> new OrderException(ORDER_NOT_FOUND));
        if (result.getStatus() == OrderStatus.SHIPPED || result.getStatus() == OrderStatus.DELIVERED) {
            throw new OrderException(ORDER_CANNOT_REJECT);
        }
        result.rejectByFranchise("관리자 요청에 의해 취소되었습니다.");
    }

    @Transactional
    public void approveOrder(Long id) {
        Order result = orderRepository.findById(id)
                .orElseThrow(() -> new OrderException(ORDER_NOT_FOUND));
        if (result.getStatus() != OrderStatus.REQUESTED) {
            throw new OrderException(ORDER_CANNOT_APPROVED);
        }
        result.approveByManager();
    }
}
