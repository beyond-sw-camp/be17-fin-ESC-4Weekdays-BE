package com.fourweekdays.fourweekdays.purchaseorder.model.entity;

import lombok.Getter;

@Getter
public enum PurchaseOrderStatus {

    REQUESTED("발주 요청"), // 승인 대기
    APPROVED("승인 완료"), // 승인됨 (공급사로 발주 전송)
    AWAITING_DELIVERY("납품 대기"), // 공급사 납품 대기 (ASN 수신)
    COMPLETED("배송 완료"),
    CANCELLED("취소"); // 취소됨

    private final String status;

    PurchaseOrderStatus(String description) {
        this.status = description;
    }

}
