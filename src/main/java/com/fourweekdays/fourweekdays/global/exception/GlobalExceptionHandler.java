package com.fourweekdays.fourweekdays.global.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BaseException.class)
    public ResponseEntity<ErrorResponse> baseExceptionHandler(BaseException e) {
        String errorClassName = e.getClass().getSimpleName();
        int httpStatusCode = e.getExceptionType().statusCode().value();
        String errorMessage = e.getExceptionType().message();

        log.error("[{}] >> {}", errorClassName, errorMessage);

        // 원인(cause) 로그 추가 — 내부 어디서 터졌는지 확인 가능
        if (e.getCause() != null) {
            log.error("[{} Cause] {}", errorClassName, e.getCause().getMessage(), e.getCause());
        }

        return ResponseEntity.ok(ErrorResponse.of(httpStatusCode, errorMessage));
    }

    // 이외의 모든 예외를 처리
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> globalExceptionHandler(Exception e) {
        log.error("[Exception] 예기치 못한 예외가 발생: {}", e.getMessage(), e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ErrorResponse.of(HttpStatus.INTERNAL_SERVER_ERROR.value(), "서버 내부 오류가 발생했습니다"));
    }
}
