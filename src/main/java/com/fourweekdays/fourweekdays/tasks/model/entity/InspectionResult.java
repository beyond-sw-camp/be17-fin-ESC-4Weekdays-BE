package com.fourweekdays.fourweekdays.tasks.model.entity;

import lombok.Getter;

@Getter
public enum InspectionResult {

    PASS("검수 통과"),
    FAIL("검수 실패 - 반품 필요"),
    PARTIAL( "일부 수량만 합격");

    private final String description;

    InspectionResult(String description) {
        this.description = description;
    }
}
