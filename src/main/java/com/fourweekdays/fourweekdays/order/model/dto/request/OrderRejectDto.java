package com.fourweekdays.fourweekdays.order.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class OrderRejectDto {
    @NotBlank(message = "주문 코드는 필수입니다")
    private String orderCode;

    @NotBlank(message = "거부 사유는 필수입니다")
    @Size(min = 10, message = "거부 사유는 최소 10자 이상이어야 합니다")
    String description;
}
