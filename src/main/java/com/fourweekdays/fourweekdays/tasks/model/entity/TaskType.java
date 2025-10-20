package com.fourweekdays.fourweekdays.tasks.model.entity;

import lombok.Getter;

@Getter
public enum TaskType {
    INBOUND("입고"),
    PUTAWAY("적치"),
    PICKING("피킹"),
    INSPECTION("검수"),
    OUTBOUND("출고"),
    INVENTORY_CHECK("재고조사"),
    RELOCATION("이동");

    private final String description;

    TaskType(String description) {
        this.description = description;
    }
}