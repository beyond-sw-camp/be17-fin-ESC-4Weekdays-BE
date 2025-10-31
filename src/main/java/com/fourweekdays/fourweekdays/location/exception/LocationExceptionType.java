package com.fourweekdays.fourweekdays.location.exception;

import com.fourweekdays.fourweekdays.global.exception.ExceptionType;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.*;

public enum LocationExceptionType implements ExceptionType {

    LOCATION_NOT_AVAILABLE(BAD_REQUEST, "사용 불가능한 공간입니다"),
    CAPACITY_EXCEEDED(BAD_REQUEST, "용량을 초과했습니다"),
    USED_CAPACITY_NEGATIVE(BAD_REQUEST, "사용량은 음수가 될 수 없습니다"),
    LOCATION_VENDOR_MISMATCH(BAD_REQUEST, "해당 위치는 다른 공급 업체의 전용 구역입니다"),

    LOCATION_NOT_FOUND(NOT_FOUND, "위치를 찾을 수 없습니다"),

    LOCATION_CODE_DUPLICATED(CONFLICT, "이미 존재하는 위치코드입니다");

    private final HttpStatus status;
    private final String message;

    LocationExceptionType(HttpStatus status, String message) {
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
