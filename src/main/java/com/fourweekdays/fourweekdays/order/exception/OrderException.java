package com.fourweekdays.fourweekdays.order.exception;

import com.fourweekdays.fourweekdays.global.exception.BaseException;
import com.fourweekdays.fourweekdays.global.exception.ExceptionType;

public class OrderException extends BaseException {

    public OrderException(ExceptionType exceptionType) {
        super(exceptionType);
    }
}
