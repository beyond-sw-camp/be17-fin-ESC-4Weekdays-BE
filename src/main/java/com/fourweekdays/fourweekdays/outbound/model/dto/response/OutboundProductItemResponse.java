package com.fourweekdays.fourweekdays.outbound.model.dto.response;

import com.fourweekdays.fourweekdays.outbound.model.entity.OutboundProductItem;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class OutboundProductItemResponse {
    private Long id;
    private Long productId;
    private String productCode;
    private String productName;
    private Integer orderedQuantity;
    private String locationCode;
    private String description;

    public static OutboundProductItemResponse from(OutboundProductItem item) {
        return OutboundProductItemResponse.builder()
                .id(item.getId())
                .productId(item.getProduct().getId())
                .productCode(item.getProduct().getProductCode())
                .productName(item.getProduct().getName())
                .orderedQuantity(item.getOrderedQuantity())
                .locationCode(item.getLocationCode())
                .description(item.getDescription())
                .build();
    }
}
