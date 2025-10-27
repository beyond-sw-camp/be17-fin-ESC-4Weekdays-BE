package com.fourweekdays.fourweekdays.order.model.dto.response;

import com.fourweekdays.fourweekdays.order.model.entity.OrderProductItem;
import lombok.Builder;

@Builder
public record OrderProductResponseDto(
        Long id,
        Long productId,
        String productName,
        Long unitPrice,
        Integer orderedQuantity,
        String description
) {
    public static OrderProductResponseDto from(OrderProductItem entity) {
        return OrderProductResponseDto.builder()
                .id(entity.getId())
                .productId(entity.getProduct().getId())
                .productName(entity.getProduct().getName())
                .unitPrice(entity.getProduct().getUnitPrice())
                .orderedQuantity(entity.getOrderedQuantity())
                .description(entity.getDescription())
                .build();
    }
}
