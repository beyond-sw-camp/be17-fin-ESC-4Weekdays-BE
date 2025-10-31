package com.fourweekdays.fourweekdays.outbound.exception;

import com.fourweekdays.fourweekdays.global.exception.ExceptionType;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

public enum OutboundExceptionType implements ExceptionType {

    OUTBOUND_STATUS_TRANSITION_NOT_ALLOWED(BAD_REQUEST, "출고 상태를 변경할 수 없습니다."),
    OUTBOUND_INVALID_STATUS_FOR_INSPECTION(BAD_REQUEST, "검수 작업을 할 수 없는 상태입니다."),
    OUTBOUND_CANNOT_CANCEL(BAD_REQUEST, "출고를 취소할 수 없습니다." ),

    OUTBOUND_NOT_FOUND(NOT_FOUND, "해당 출고를 찾을 수 없습니다."),
    OUTBOUND_PRODUCT_NOT_FOUND(NOT_FOUND, "출고 상품을 찾을 수 없습니다."),

    OUTBOUND_ORDER_EXISTENCE (BAD_REQUEST, "중복된 출고 요청."),
    ;


    private final HttpStatus httpStatus;
    private final String message;

    OutboundExceptionType(HttpStatus httpStatus, String message) {
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
