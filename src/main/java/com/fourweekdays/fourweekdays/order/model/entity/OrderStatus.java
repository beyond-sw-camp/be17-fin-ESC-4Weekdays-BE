package com.fourweekdays.fourweekdays.order.model.entity;

import lombok.Getter;

@Getter
public enum OrderStatus {
    REQUESTED("주문 생성"), // 승인 대기
    APPROVED("승인 완료"), // 승인됨
    SHIPPED("출하 완료"), // 출하됨
    DELIVERED("배송 완료"), // 배송 완료됨
    CANCELLED("취소"); // 취소됨

    private final String description;

    OrderStatus(String description) {
        this.description = description;
    }
}