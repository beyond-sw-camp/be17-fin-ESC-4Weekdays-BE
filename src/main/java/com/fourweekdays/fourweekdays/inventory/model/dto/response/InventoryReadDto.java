package com.fourweekdays.fourweekdays.inventory.model.dto.response;

import com.fourweekdays.fourweekdays.inventory.model.entity.Inventory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InventoryReadDto {

    private Long id;

    private Long productId;
    private String productName;

    private Long locationId;
    private String locationCode;

    private Long inboundId;
    private String lotNumber;
    private Integer quantity;

    private String description;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static InventoryReadDto from(Inventory inventory) {
        return InventoryReadDto.builder()
                .id(inventory.getId())

                .productId(inventory.getProduct().getId())
                .productName(inventory.getProduct().getName())

                .locationId(inventory.getLocation().getId())
                .locationCode(inventory.getLocation().getLocationCode())

                .lotNumber(inventory.getLotNumber())
                .quantity(inventory.getQuantity())

                .description(inventory.getDescription())

                .createdAt(inventory.getCreatedAt())
                .updatedAt(inventory.getUpdatedAt())

                .build();
    }
}

