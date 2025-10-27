package com.fourweekdays.fourweekdays.tasks.model.dto.request;

import jakarta.validation.constraints.NotBlank;

public record TaskCompleteRequest(
        Long taskId,

        @NotBlank(message = "작업 메모는 필수입니다")
        String note
) {
}






