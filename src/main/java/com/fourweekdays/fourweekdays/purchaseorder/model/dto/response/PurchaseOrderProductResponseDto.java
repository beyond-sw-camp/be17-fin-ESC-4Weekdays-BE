package com.fourweekdays.fourweekdays.purchaseorder.model.dto.response;

import com.fourweekdays.fourweekdays.purchaseorder.model.entity.PurchaseOrderProduct;
import lombok.Builder;

@Builder
public record PurchaseOrderProductResponseDto(
        Long id,
        Long productId,
        String productName,
        Long unitPrice,
        Integer orderedQuantity,
        String description
) {
    public static PurchaseOrderProductResponseDto toDto(PurchaseOrderProduct item) {
        return PurchaseOrderProductResponseDto.builder()
                .id(item.getId())
                .productId(item.getProduct().getId())
                .productName(item.getProduct().getName())
                .unitPrice(item.getProduct().getUnitPrice())
                .orderedQuantity(item.getOrderedQuantity())
                .description(item.getDescription())
                .build();
    }
}