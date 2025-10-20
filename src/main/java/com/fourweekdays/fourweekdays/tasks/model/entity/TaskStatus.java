package com.fourweekdays.fourweekdays.tasks.model.entity;

import lombok.Getter;

@Getter
public enum TaskStatus {
    PENDING("대기중"),
    ASSIGNED("할당됨"),
    IN_PROGRESS("진행중"),
    COMPLETED("완료"),
    CANCELLED("취소");

    private final String description;

    TaskStatus(String description) {
        this.description = description;
    }
}
