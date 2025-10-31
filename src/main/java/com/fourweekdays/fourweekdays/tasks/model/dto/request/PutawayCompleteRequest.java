package com.fourweekdays.fourweekdays.tasks.model.dto.request;

import jakarta.validation.constraints.NotBlank;

public record PutawayCompleteRequest(
        @NotBlank(message = "적치 위치는 필수입니다")
        String locationCode, // 예: "01-A"

        @NotBlank(message = "작업 메모는 필수입니다")
        String note
) {
}
