package com.fourweekdays.fourweekdays.product.exception;

import com.fourweekdays.fourweekdays.global.exception.BaseException;
import com.fourweekdays.fourweekdays.global.exception.ExceptionType;

/**
 * 상품 도메인 전용 예외
 * 공통 BaseException 상속
 */
public class ProductException extends BaseException {

    public ProductException(ExceptionType exceptionType) {
        super(exceptionType);
    }

    public ProductException(ExceptionType exceptionType, Throwable cause) {
        super(exceptionType, cause);
    }
}
