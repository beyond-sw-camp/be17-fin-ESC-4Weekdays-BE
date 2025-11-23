package com.fourweekdays.fourweekdays.tasks.controller;

import jakarta.validation.constraints.NotBlank;

public record TaskPackingCompleteRequest(
//        @NotBlank(message = "작업 메모는 필수입니다")
        String note
) {
}
