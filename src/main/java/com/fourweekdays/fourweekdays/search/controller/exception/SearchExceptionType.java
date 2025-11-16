package com.fourweekdays.fourweekdays.search.controller.exception;

import com.fourweekdays.fourweekdays.global.exception.ExceptionType;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.*;

public enum SearchExceptionType implements ExceptionType {

    DOMAIN_NOT_FOUND(NOT_FOUND, "유효하지 않은 도메인"),
    ;

    private final HttpStatus httpStatus;
    private final String message;

    SearchExceptionType(HttpStatus httpStatus, String message) {
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
