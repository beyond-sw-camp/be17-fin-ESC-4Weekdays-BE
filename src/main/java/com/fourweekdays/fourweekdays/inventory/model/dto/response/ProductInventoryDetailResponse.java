package com.fourweekdays.fourweekdays.inventory.model.dto.response;

import com.fourweekdays.fourweekdays.inventory.model.entity.Inventory;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;


@Getter
@Builder
public class ProductInventoryDetailResponse {
    private Long id;
    private String locationCode;
    private String lotNumber;
    private Integer quantity;
    private String inboundCode;
    private LocalDateTime createdAt;

    public static ProductInventoryDetailResponse from(Inventory inventory) {
        return ProductInventoryDetailResponse.builder()
                .id(inventory.getId())
                .locationCode(inventory.getLocation().getLocationCode())
                .lotNumber(inventory.getLotNumber())
                .quantity(inventory.getQuantity())
                .inboundCode(inventory.getInbound() != null ? inventory.getInbound().getInboundCode() : null)
                .createdAt(inventory.getCreatedAt())
                .build();
    }
}
