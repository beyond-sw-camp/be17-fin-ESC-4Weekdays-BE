package com.fourweekdays.fourweekdays.announcement.exception;


import com.fourweekdays.fourweekdays.global.exception.ExceptionType;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Getter
public enum AnnouncementExceptionType implements ExceptionType {

    ANNOUNCEMENT_NOT_FOUND(NOT_FOUND, "공지를 찾을 수 없습니다.");

    private final HttpStatus httpStatus;
    private final String message;

    AnnouncementExceptionType(HttpStatus httpStatus, String message) {
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
