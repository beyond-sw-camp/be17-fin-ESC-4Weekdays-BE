package com.fourweekdays.fourweekdays.inventory.exception;

import com.fourweekdays.fourweekdays.global.exception.ExceptionType;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.*;

public enum InventoryExceptionType implements ExceptionType {

    INSUFFICIENT_INVENTORY(BAD_REQUEST, "재고 수량이 부족합니다."),
    INVALID_INCREASE_QUANTITY(BAD_REQUEST, "추가 수량은 0보다 커야 합니다."),
    INVALID_DECREASE_QUANTITY(BAD_REQUEST, "감소 수량은 0보다 커야 합니다."),

    INVENTORY_NOT_FOUND(NOT_FOUND, "해당 재고를 찾을 수 없습니다."),

    INVENTORY_SAVE_FAILED(INTERNAL_SERVER_ERROR, "재고 저장 중 오류가 발생했습니다.");

    private final HttpStatus httpStatus;
    private final String message;

    InventoryExceptionType(HttpStatus httpStatus, String message) {
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

