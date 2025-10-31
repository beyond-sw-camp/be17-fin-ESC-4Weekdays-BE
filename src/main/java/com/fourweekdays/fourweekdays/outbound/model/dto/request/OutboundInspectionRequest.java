package com.fourweekdays.fourweekdays.outbound.model.dto.request;

import lombok.Getter;

@Getter
public class OutboundInspectionRequest {
    private Long OutboundProductid;
    private Integer orderedQuantity;
}
