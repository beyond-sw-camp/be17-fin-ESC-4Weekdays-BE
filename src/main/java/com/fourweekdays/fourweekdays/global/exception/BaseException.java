package com.fourweekdays.fourweekdays.global.exception;

import lombok.Getter;

@Getter
public abstract class BaseException extends RuntimeException {
    private final ExceptionType exceptionType;

    // 기본 생성자 (기존)
    public BaseException(ExceptionType exceptionType) {
        super(exceptionType.message()); // 예외 메시지 포함
        this.exceptionType = exceptionType;
    }

    // cause(원인 예외)까지 전달 가능한 생성자 추가
    public BaseException(ExceptionType exceptionType, Throwable cause) {
        super(exceptionType.message(), cause);
        this.exceptionType = exceptionType;
    }
}
