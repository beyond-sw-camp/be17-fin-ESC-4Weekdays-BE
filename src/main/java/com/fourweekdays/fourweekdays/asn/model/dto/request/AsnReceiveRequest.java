package com.fourweekdays.fourweekdays.asn.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public record AsnReceiveRequest(
        @NotBlank(message = "발주 코드는 필수입니다")
        String orderCode,

        @NotBlank(message = "거부 사유는 필수입니다")
        @Size(min = 10, message = "거부 사유는 최소 10자 이상이어야 합니다")
        String description,

        LocalDateTime expectedDate
) {
}
