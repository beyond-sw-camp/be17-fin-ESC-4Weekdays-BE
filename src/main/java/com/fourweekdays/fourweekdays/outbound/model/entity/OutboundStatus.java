package com.fourweekdays.fourweekdays.outbound.model.entity;

import lombok.Getter;

@Getter
public enum OutboundStatus {

    REQUESTED("출고서 생성"),
    APPROVED("출고 예정"),
    PICKING("피킹 완료"),
    INSPECTION("검수 완료"),
    PACKING("패킹 완료"),
    SHIPPED("출하 완료"), // 재고 감소
    CANCELLED("취소");

    private final String description;

    OutboundStatus(String description) {
        this.description = description;
    }

    public boolean canTransitionTo(OutboundStatus next) {
        if (next == CANCELLED) return true;

        return switch (this) {
            case REQUESTED -> next == APPROVED;
            case APPROVED -> next == PICKING;
            case PICKING -> next == INSPECTION;
            case INSPECTION -> next == PACKING;
            case PACKING -> next == SHIPPED;
            case SHIPPED, CANCELLED -> false;
        };
    }
}
