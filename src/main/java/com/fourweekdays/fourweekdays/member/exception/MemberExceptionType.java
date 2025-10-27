package com.fourweekdays.fourweekdays.member.exception;

import com.fourweekdays.fourweekdays.global.exception.ExceptionType;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.NOT_FOUND;

public enum MemberExceptionType implements ExceptionType {

    MEMBER_NOT_FOUND(NOT_FOUND, "해당 직원을 찾을 수 없습니다."),
    DUPLICATE_EMAIL(HttpStatus.CONFLICT, "이미 가입된 이메일입니다.");

    private final HttpStatus httpStatus;
    private final String message;

    MemberExceptionType(HttpStatus httpStatus, String message) {
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
