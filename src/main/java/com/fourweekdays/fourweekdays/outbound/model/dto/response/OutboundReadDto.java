package com.fourweekdays.fourweekdays.outbound.model.dto.response;

import com.fourweekdays.fourweekdays.outbound.model.entity.Outbound;
import lombok.Getter;

@Getter
public class OutboundReadDto {
    private Long id;
    private String vendorName;
    private String productName;
    private int quantity;
    private String outboundType;
    private String status;

    public OutboundReadDto(Outbound outbound){
        this.id = outbound.getId();
//        this.vendorName = outbound.getVendor().getName();
//        this.productName = outbound.getProduct().getName();
//        this.quantity = outbound.getQuantity();
        this.outboundType = outbound.getOutboundType().toString();
        this.status = outbound.getStatus().name();
    }
}
