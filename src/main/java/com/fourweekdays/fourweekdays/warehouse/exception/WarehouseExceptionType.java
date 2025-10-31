package com.fourweekdays.fourweekdays.warehouse.exception;

import com.fourweekdays.fourweekdays.global.exception.ExceptionType;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

public enum WarehouseExceptionType implements ExceptionType {

    WAREHOUSE_NOT_FOUND(NOT_FOUND, "해당 물류창고를 찾을 수 없습니다."),
    ;

    private final HttpStatus httpStatus;
    private final String message;

    WarehouseExceptionType(HttpStatus httpStatus, String message) {
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
