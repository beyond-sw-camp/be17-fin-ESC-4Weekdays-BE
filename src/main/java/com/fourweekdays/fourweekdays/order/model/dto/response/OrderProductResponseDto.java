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
    public static OrderProductResponseDto from(OrderProductItem item) {
        return OrderProductResponseDto.builder()
                .id(item.getId())
                .productId(item.getProduct().getId())
                .productName(item.getProduct().getName())
                .unitPrice(item.getProduct().getUnitPrice())
                .orderedQuantity(item.getOrderedQuantity())
                .description(item.getDescription())
                .build();
    }
}
