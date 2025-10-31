package com.fourweekdays.fourweekdays.inbound.model.dto.response;


import com.fourweekdays.fourweekdays.inbound.model.entity.InboundProduct;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;


@Getter
@Builder
@AllArgsConstructor
public class InboundProductResponseDto {

    private Long id;

    // Product 정보
    private Long productId;
    private String productCode;
    private String productName;

    // 입고 정보
    private Integer receivedQuantity;  // Long → Integer, quantity → receivedQuantity
    private String description;

    // 발주 정보 (발주 기반 입고인 경우)
    private Long purchaseOrderProductItemId;  // 발주 항목 ID
    private Integer orderedQuantity;  // 발주 수량 (비교용)
    private Boolean isFromPurchaseOrder;  // 발주 품목인지 추가 품목인지

    public static InboundProductResponseDto from(InboundProduct item) {
        return InboundProductResponseDto.builder()
                .id(item.getId())

                .productId(item.getProduct().getId())
                .productCode(item.getProduct().getProductCode())
                .productName(item.getProduct().getName())

                .receivedQuantity(item.getReceivedQuantity())
                .description(item.getDescription())

                .purchaseOrderProductItemId(
                        item.getPurchaseOrderProduct() != null ?
                                item.getPurchaseOrderProduct().getId() : null)
                .orderedQuantity(
                        item.getPurchaseOrderProduct() != null ?
                                item.getPurchaseOrderProduct().getOrderedQuantity() : null)
                .isFromPurchaseOrder(item.getPurchaseOrderProduct() != null)
                .build();
    }
}
