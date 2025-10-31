package com.fourweekdays.fourweekdays.tasks.exception;

import com.fourweekdays.fourweekdays.global.exception.ExceptionType;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.*;

public enum TaskExceptionType implements ExceptionType {

    TASK_NOT_FOUND(NOT_FOUND, "작업을 찾을 수 없습니다"),
    INSPECTION_TASK_NOT_FOUND(NOT_FOUND, "검수 작업 상세를 찾을 수 없습니다"),
    PUTAWAY_TASK_NOT_FOUND(NOT_FOUND, "적치 작업 상세를 찾을 수 없습니다"),

    TASK_CANNOT_ASSIGN(BAD_REQUEST, "대기 중인 작업만 할당할 수 있습니다"),
    TASK_CANNOT_START(BAD_REQUEST, "할당된 작업만 시작할 수 있습니다"),
    TASK_CANNOT_COMPLETE(BAD_REQUEST, "진행 중인 작업만 완료할 수 있습니다"),
    TASK_CANNOT_CANCEL(BAD_REQUEST, "완료된 작업은 취소할 수 없습니다"),

    PUTAWAY_LOCATION_ALREADY_ASSIGNED(CONFLICT, "이미 위치 할당된 적치 작업입니다.");


    private final HttpStatus status;
    private final String message;

    TaskExceptionType(HttpStatus status, String message) {
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
