package com.fourweekdays.fourweekdays.purchaseorder.model.dto.response;

import com.fourweekdays.fourweekdays.purchaseorder.model.entity.PurchaseOrder;
import com.fourweekdays.fourweekdays.purchaseorder.model.entity.PurchaseOrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;


@Getter
@AllArgsConstructor
@Builder
public class PurchaseOrderReadDto {

    private final Long id;
    private final String orderNumber; // 발주번호
    private final String vendorName;
    private final LocalDateTime orderDate; // 발주일
    private final LocalDateTime expectedDate; // 입고예정일
    private final PurchaseOrderStatus status; // 상태
    private final Long totalAmount; // 총 금액
    private final String description; // 비고
    private final List<PurchaseOrderProductResponseDto> items; // 발주 상품 목록

    public static PurchaseOrderReadDto toDto(PurchaseOrder entity) {
        return PurchaseOrderReadDto.builder()
                .id(entity.getId())
                .orderNumber(entity.getOrderCode())
                .vendorName(entity.getVendor().getName())
                .orderDate(entity.getOrderDate())
                .expectedDate(entity.getExpectedDate())
                .status(entity.getStatus())
                .totalAmount(entity.calculateTotalAmount())
                .description(entity.getDescription())
                .items(entity.getProducts().stream()
                        .map(PurchaseOrderProductResponseDto::toDto)
                        .toList())
                .build();
    }
}