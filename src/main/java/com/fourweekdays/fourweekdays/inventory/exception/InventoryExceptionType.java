package com.fourweekdays.fourweekdays.inventory.exception;

import com.fourweekdays.fourweekdays.global.exception.ExceptionType;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.*;

public enum InventoryExceptionType implements ExceptionType {

    INSUFFICIENT_INVENTORY(BAD_REQUEST, "재고가 부족합니다"),

    INVALID_QUANTITY(BAD_REQUEST, "유효하지 않은 수량입니다"),
    INVENTORY_NOT_FOUND(NOT_FOUND, "재고를 찾을 수 없습니다"),

    INVENTORY_ALREADY_EXISTS(CONFLICT, "이미 존재하는 재고입니다");

    private final HttpStatus status;
    private final String message;

    InventoryExceptionType(HttpStatus status, String message) {
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
