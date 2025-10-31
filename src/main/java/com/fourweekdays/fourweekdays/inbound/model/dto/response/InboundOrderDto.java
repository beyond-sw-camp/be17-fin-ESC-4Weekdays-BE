package com.fourweekdays.fourweekdays.inbound.model.dto.response;

import com.fourweekdays.fourweekdays.purchaseorder.model.entity.PurchaseOrder;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class InboundOrderDto {

    private Long id;
    private String orderNumber;
    private String vendorName;
    private LocalDateTime orderDate;

    public static InboundOrderDto from(PurchaseOrder order) {
        if (order == null) return null;

        return InboundOrderDto.builder()
                .id(order.getId())
                .orderNumber(order.getOrderCode())
                .vendorName(order.getVendor() != null ? order.getVendor().getName() : null)
                .orderDate(order.getOrderDate())
                .build();
    }

}