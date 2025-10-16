package com.fourweekdays.fourweekdays.outbound.model.dto.request;

import com.fourweekdays.fourweekdays.outbound.model.entity.Outbound;
import com.fourweekdays.fourweekdays.outbound.model.entity.OutboundStatus;
import com.fourweekdays.fourweekdays.outbound.model.entity.OutboundType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
@Schema(description = "출고 요청 등록 DTO")
public class OutboundCreateDto {

    @Schema(description = "출고 수량(1개 이상)", example = "50")
    @Min(value = 1, message = "출고 수량은 1개 이상이어야 합니다.")
    private int quantity;

    @Schema(description = "출고 유형 (SALE: 판매, RETURN: 반품, TRANSFER: 이동)", example = "SALE")
    @NotNull(message = "출고 유형은 필수입니다.")
    private OutboundType outboundType;

    public Outbound toEntity() {
        return Outbound.builder()
                .quantity(quantity)
                .outboundType(outboundType)
                .status(OutboundStatus.PENDING)
                .build();
    }
}

