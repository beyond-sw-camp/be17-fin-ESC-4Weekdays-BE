package com.fourweekdays.fourweekdays.order.model.dto.response;

import com.fourweekdays.fourweekdays.order.model.entity.Order;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class OrderReadDto {
    private Long id;
    private String orderCode;
    private String franchiseName;
    private String status;
    private String description;
    private String rejectReason;
    private LocalDateTime createAt;
    private LocalDateTime dueDate;
    private LocalDateTime rejectedAt;
    private List<OrderProductResponseDto> items;

    public static OrderReadDto from(Order entity) {
        return OrderReadDto.builder()
                .id(entity.getOrderId())
                .orderCode(entity.getOrderCode())
                .franchiseName(entity.getFranchiseStore().getName())
                .status(entity.getStatus().toString())
                .description(entity.getDescription())
                .rejectReason(entity.getRejectedReason())
                .createAt(entity.getCreatedAt())
                .dueDate(entity.getDueDate())
                .rejectedAt(entity.getRejectedAt())
                .items(entity.getItems().stream()
                        .map(OrderProductResponseDto::from)
                        .toList())
                .build();
    }
}
