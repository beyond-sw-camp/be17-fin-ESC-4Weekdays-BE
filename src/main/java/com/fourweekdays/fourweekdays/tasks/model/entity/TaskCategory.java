package com.fourweekdays.fourweekdays.tasks.model.entity;

public enum TaskCategory {

    INSPECTION("검수"),
    PUTAWAY("적치"),
    PICKING("피킹"),
    PACKING("포장");

    private final String description;

    TaskCategory(String description) {
        this.description = description;
    }
}
