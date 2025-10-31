package com.fourweekdays.fourweekdays.inbound.model.dto.response;

import com.fourweekdays.fourweekdays.inbound.model.entity.Inbound;
import com.fourweekdays.fourweekdays.inbound.model.entity.InboundStatus;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class InboundReadDto {


    private Long id;
    private String inboundNumber;
    private InboundStatus status;

    // 담당자 정보
//    private Long memberId;
    private String managerName;

    private LocalDateTime scheduledDate;
//    private LocalDateTime receivedDate;
//    private LocalDateTime completedDate;
    
    private PurchaseOrderSummary purchaseOrder; // 발주 상세정보
    private List<InboundProductResponseDto> items;

    private String description;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Getter
    @Builder
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @AllArgsConstructor
    public static class PurchaseOrderSummary {
        private Long id;
        private String orderNumber;
        private String vendorName;
        private LocalDateTime orderDate;
    }
    private InboundOrderDto order;

    public static InboundReadDto from(Inbound inbound) {
        return InboundReadDto.builder()
                .id(inbound.getId())
                .inboundNumber(inbound.getInboundCode())
                .status(inbound.getStatus())
                .managerName(inbound.getManagerName())
                .scheduledDate(inbound.getScheduledDate())
                .order(InboundOrderDto.from(inbound.getPurchaseOrder()))
                .items(inbound.getProducts().stream()
                        .map(InboundProductResponseDto::from)
                        .toList())
                .purchaseOrder(inbound.getPurchaseOrder() != null ?
                        PurchaseOrderSummary.builder()
                                .id(inbound.getPurchaseOrder().getId())
                                .orderNumber(inbound.getPurchaseOrder().getOrderCode())
                                .vendorName(inbound.getPurchaseOrder().getVendor().getName())
                                .orderDate(inbound.getPurchaseOrder().getOrderDate())
                                .build()
                        : null)
                .items(inbound.getProducts().stream()
                        .map(InboundProductResponseDto::from)
                        .toList())
                .description(inbound.getDescription())
                .createdAt(inbound.getCreatedAt())
                .updatedAt(inbound.getUpdatedAt())
                .build();
    }

}
