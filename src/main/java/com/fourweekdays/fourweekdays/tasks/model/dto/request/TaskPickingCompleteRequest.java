package com.fourweekdays.fourweekdays.tasks.model.dto.request;

import jakarta.validation.constraints.NotNull;

public record TaskPickingCompleteRequest(
//        @NotBlank(message = "작업자는 필수입니다.")
        Long workerId,

        String note,
        String locationCode
) {
}
