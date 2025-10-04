package com.fourweekdays.fourweekdays.global.exception;

import org.springframework.http.HttpStatus;

public interface ExceptionType {
    HttpStatus statusCode();
    String message();
}
