package com.fourweekdays.fourweekdays.inbound.model.dto.request;

import jakarta.validation.Valid;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class InboundCreateRequestDto {

    private Long memberId;

    private Long purchaseOrderId;  // 발주서 기반 입고
    @Valid
    private List<InboundProductDto> items;  // Optional: 직접/추가 품목

    private LocalDateTime scheduledDate; // 입고 예상 시간
    private String description;

    public void validate() {
        if (purchaseOrderId == null && (items == null || items.isEmpty())) {
            throw new IllegalArgumentException(
                    "발주서 ID 또는 품목 정보 중 최소 하나는 필수입니다.");
        }
    }
}