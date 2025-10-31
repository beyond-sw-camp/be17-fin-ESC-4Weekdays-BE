package com.fourweekdays.fourweekdays.order.service;

import com.fourweekdays.fourweekdays.common.generator.CodeGenerator;
import com.fourweekdays.fourweekdays.franchise.model.entity.FranchiseStore;
import com.fourweekdays.fourweekdays.order.exception.OrderException;
import com.fourweekdays.fourweekdays.order.model.dto.request.OrderProductDto;
import com.fourweekdays.fourweekdays.order.model.dto.request.OrderReceiveOrderDto;
import com.fourweekdays.fourweekdays.order.model.dto.request.OrderRejectDto;
import com.fourweekdays.fourweekdays.order.model.entity.Order;
import com.fourweekdays.fourweekdays.order.model.entity.OrderProductItem;
import com.fourweekdays.fourweekdays.order.model.entity.OrderStatus;
import com.fourweekdays.fourweekdays.order.repository.OrderRepository;
import com.fourweekdays.fourweekdays.product.exception.ProductException;
import com.fourweekdays.fourweekdays.product.model.entity.Product;
import com.fourweekdays.fourweekdays.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static com.fourweekdays.fourweekdays.order.exception.OrderExceptionType.*;
import static com.fourweekdays.fourweekdays.product.exception.ProductExceptionType.PRODUCT_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class OrderFranchiseService {

    private static final String ORDER_CODE_PREFIX = "ORD";

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final CodeGenerator codeGenerator;

    @Transactional
    public Long receiveCreateOrder(FranchiseStore franchiseStore, OrderReceiveOrderDto dto) {
        // franchise 와 관계를 맺은 order가 생성되게 하기
        Order order = createOrder(franchiseStore, dto);
        createOrderProducts(dto.getItems(), order);

        return orderRepository.save(order).getOrderId();
    }

    @Transactional
    public void rejectOrder(FranchiseStore franchiseStore, OrderRejectDto dto) {
        Order order = findValidateOrder(franchiseStore, dto.getOrderCode());

        if (order.getStatus() != OrderStatus.APPROVED) {
            throw new OrderException(ORDER_CANNOT_REJECT);
        }

        order.rejectByFranchise(dto.getDescription());
    }

    private Order findValidateOrder(FranchiseStore franchiseStore, String orderCode) {
        Order order = orderRepository.findByOrderCode(orderCode)
                .orElseThrow(() -> new OrderException(ORDER_NOT_FOUND));

        Long orderFranchiseId = order.getFranchiseStore().getId();
        if (!orderFranchiseId.equals(franchiseStore.getId())) {
            throw new OrderException(FRANCHISE_MISMATCH);
        }

        return order;
    }

    private Order createOrder(FranchiseStore franchiseStore, OrderReceiveOrderDto dto) {
        return Order.builder()
                .franchiseStore(franchiseStore)
                .orderCode(codeGenerator.generate(ORDER_CODE_PREFIX))
                .dueDate(dto.getDueDate())
                .description(dto.getDescription())
                .status(OrderStatus.REQUESTED)
                .orderDate(LocalDateTime.now())
                .build();
    }

    private List<OrderProductItem> createOrderProducts(List<OrderProductDto> items, Order order) {
        return items.stream()
                .map(item -> createProduct(item, order))
                .toList();
    }

    private OrderProductItem createProduct(OrderProductDto dto, Order order) {
        Product product = productRepository.findById(dto.getProductId())
                .orElseThrow(() -> new ProductException(PRODUCT_NOT_FOUND));

        OrderProductItem orderProductItem = OrderProductItem.builder()
                .product(product)
                .orderedQuantity(dto.getOrderedQuantity())
                .description(dto.getDescription())
                .build();

        order.addItem(orderProductItem);
        return orderProductItem;
    }
}
