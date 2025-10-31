package com.fourweekdays.fourweekdays.location.model.entity;

public enum LocationStatus {

    AVAILABLE("사용가능"),
    CLOSED("폐쇄");

    private final String description;

    LocationStatus(String description) {
        this.description = description;
    }
}
