package com.fourweekdays.fourweekdays.inbound.model.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class InboundProductDto {

    @NotNull(message = "상품 ID는 필수입니다.")
    private Long productId;

    @NotNull(message = "수량이 없습니다.")
    @Min(value = 1, message = "품목은 1개 미만일 수 없습니다.")
    private Integer quantity; // 수량

    private String description;

}