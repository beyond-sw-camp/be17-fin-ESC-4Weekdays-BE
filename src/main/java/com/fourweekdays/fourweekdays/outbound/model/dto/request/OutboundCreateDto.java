package com.fourweekdays.fourweekdays.outbound.model.dto.request;

import com.fourweekdays.fourweekdays.inbound.model.dto.request.InboundProductDto;
import com.fourweekdays.fourweekdays.member.model.entity.Member;
import com.fourweekdays.fourweekdays.order.model.entity.Order;
import com.fourweekdays.fourweekdays.outbound.model.entity.Outbound;
import com.fourweekdays.fourweekdays.outbound.model.entity.OutboundStatus;
import com.fourweekdays.fourweekdays.outbound.model.entity.OutboundType;
import lombok.Builder;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@Schema(description = "출고 요청 등록 DTO")
public class OutboundCreateDto {

    private Long memberId;
    private Long orderId;
    private LocalDateTime scheduledDate; // 출고 예상 시간
    private String description;
//     private List<InboundProductDto> items; // 직접/추가

    public Outbound toEntity(String outboundCode, OutboundStatus status) {
        return Outbound.builder()
                .outboundManager(Member.builder().id(memberId).build())
                .order(Order.builder().orderId(orderId).build())
                .scheduledDate(scheduledDate)
                .description(description)
                .outboundCode(outboundCode)
                .status(status)
                .build();
    }
}

