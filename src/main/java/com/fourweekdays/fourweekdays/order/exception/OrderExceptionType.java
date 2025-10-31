package com.fourweekdays.fourweekdays.order.exception;

import com.fourweekdays.fourweekdays.global.exception.ExceptionType;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

public enum OrderExceptionType implements ExceptionType {

    ORDER_NOT_FOUND(NOT_FOUND, "해당 주문을 찾을 수 없습니다."),
    FRANCHISE_MISMATCH(BAD_REQUEST, "주문의 거래처가 일치하지 않습니다."),
    ORDER_CANNOT_REJECT(BAD_REQUEST, "현제 상태에서는 거절할 수 없습니다."),
    ORDER_CANNOT_APPROVED(BAD_REQUEST, "현제 상태에서는 출고서를 요청할수 없습니다.")
    ;

    private final HttpStatus status;
    private final String message;

    OrderExceptionType(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }

    @Override
    public HttpStatus statusCode() {
        return this.status;
    }

    @Override
    public String message() {
        return this.message;
    }
}
