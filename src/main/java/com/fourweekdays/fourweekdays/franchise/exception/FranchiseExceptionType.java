package com.fourweekdays.fourweekdays.franchise.exception;

import com.fourweekdays.fourweekdays.global.exception.ExceptionType;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

public enum FranchiseExceptionType implements ExceptionType {

    FRANCHISE_NOT_FOUND(NOT_FOUND, "해당 가맹점을 찾을 수 없습니다."),
    FRANCHISE_INVALID_STATUS(BAD_REQUEST, "잘못된 상태 변경 요청입니다.")
    ;

    private final HttpStatus httpStatus;
    private final String message;

    FranchiseExceptionType(HttpStatus httpStatus, String message) {
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
