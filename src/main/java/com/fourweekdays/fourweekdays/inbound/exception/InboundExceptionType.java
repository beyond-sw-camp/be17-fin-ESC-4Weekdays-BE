package com.fourweekdays.fourweekdays.inbound.exception;

import com.fourweekdays.fourweekdays.global.exception.ExceptionType;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

public enum InboundExceptionType implements ExceptionType {

    INBOUND_STATUS_TRANSITION_NOT_ALLOWED(BAD_REQUEST, "입고 상태를 변경할 수 없습니다."),
    INBOUND_INVALID_STATUS_FOR_INSPECTION(BAD_REQUEST, "검수 작업을 할 수 없는 상태입니다."),
    INBOUND_CANNOT_CANCEL(BAD_REQUEST, "입고를 취소할 수 없습니다." ),

    INBOUND_NOT_FOUND(NOT_FOUND, "해당 입고를 찾을 수 없습니다."),
    INBOUND_PRODUCT_NOT_FOUND(NOT_FOUND, "입고 상품을 찾을 수 없습니다."), ;


    private final HttpStatus httpStatus;
    private final String message;

    InboundExceptionType(HttpStatus httpStatus, String message) {
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
