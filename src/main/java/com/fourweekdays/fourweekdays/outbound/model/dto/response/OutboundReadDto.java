package com.fourweekdays.fourweekdays.outbound.model.dto.response;

import com.fourweekdays.fourweekdays.member.model.entity.Member;
import com.fourweekdays.fourweekdays.order.model.entity.Order;
import com.fourweekdays.fourweekdays.outbound.model.entity.Outbound;
import com.fourweekdays.fourweekdays.outbound.model.entity.OutboundProductItem;
import com.fourweekdays.fourweekdays.outbound.model.entity.OutboundStatus;
import com.fourweekdays.fourweekdays.outbound.model.entity.OutboundType;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class OutboundReadDto {
    private Long id;
    private String outboundCode;
    private OutboundType outboundType; // 출고유형
    private OutboundStatus status; // 출고상태
    private Long orderId; // 주문 기반 출고 생성
    private String outboundManagerName; // 출고 등록인
    private LocalDateTime scheduledDate; // 출고 예상 일시
    private String description;
    private List<OutboundProductItemResponse> outboundProductItems;

    public static OutboundReadDto from(Outbound outbound){
        return OutboundReadDto.builder()
                .id(outbound.getId())
                .outboundCode(outbound.getOutboundCode())
                .outboundType(outbound.getOutboundType())
                .status(outbound.getStatus())
                .orderId(outbound.getOrder().getOrderId())
                .outboundManagerName(outbound.getOutboundManager().getName())
                .scheduledDate(outbound.getScheduledDate())
                .description(outbound.getDescription())
                .outboundProductItems(
                        outbound.getItems()
                                .stream()
                                .map(OutboundProductItemResponse::from)
                                .toList()
                )
                .build();
    }
}
