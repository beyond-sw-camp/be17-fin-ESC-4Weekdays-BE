package com.fourweekdays.fourweekdays.purchaseorder.exception;

import com.fourweekdays.fourweekdays.global.exception.ExceptionType;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.*;

public enum PurchaseOrderExceptionType implements ExceptionType {

    PURCHASE_ORDER_CANNOT_REJECT(BAD_REQUEST, "현재 상태에서는 거절할 수 없습니다"),
    PURCHASE_ORDER_INVALID_STATUS_FOR_ASN(BAD_REQUEST, "ASN을 수신할 수 있는 상태가 아닙니다."),

    PURCHASE_ORDER_NOT_FOUND(NOT_FOUND, "해당 발주서를 찾을 수 없습니다."),

    PURCHASE_ORDER_CANNOT_UPDATE_AFTER_APPROVAL(CONFLICT, "이미 승인된 발주서는 수정할 수 없습니다."),
    PURCHASE_ORDER_CANNOT_CANCEL_AFTER_APPROVAL(CONFLICT, "이미 승인된 발주서는 취소할 수 없습니다."),
    PURCHASE_ORDER_INVALID_STATUS_CHANGE(CONFLICT, "현재 상태에서는 요청하신 상태로 변경할 수 없습니다."),

    PURCHASE_ORDER_TOTAL_AMOUNT_ERROR(INTERNAL_SERVER_ERROR, "총 금액 계산 중 오류가 발생했습니다."),
    PURCHASE_ORDER_PRODUCT_NOT_FOUND(NOT_FOUND, "발주 품목에 해당 상품이 존재하지 않습니다."),
    PURCHASE_ORDER_VENDOR_MISMATCH(BAD_REQUEST, "선택한 공급업체와 상품의 공급업체가 일치하지 않습니다.");

    private final HttpStatus httpStatus;
    private final String message;

    PurchaseOrderExceptionType(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }

    @Override
    public HttpStatus statusCode() {
        return this.httpStatus;
    }

    @Override
    public String message() {
        return this.message;
    }
}
