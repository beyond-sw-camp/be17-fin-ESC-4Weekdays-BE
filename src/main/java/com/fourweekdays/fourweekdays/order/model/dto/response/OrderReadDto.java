package com.fourweekdays.fourweekdays.order.model.dto.response;

import com.fourweekdays.fourweekdays.order.model.entity.Order;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class OrderReadDto {
    private Long id;
    private String orderCode;
    private String franchiseName;
    private LocalDateTime dueDate;
    private String description;

    private List<OrderProductResponseDto> products;

    public static OrderReadDto from(Order entity) {
        return OrderReadDto.builder()
                .id(entity.getOrderId())
                .orderCode(entity.getOrderCode())
                .dueDate(entity.getDueDate())
                .description(entity.getDescription())
                .products(entity.getItems().stream()
                        .map(OrderProductResponseDto::from)
                        .toList())
                .build();
    }
}
