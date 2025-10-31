package com.fourweekdays.fourweekdays.outbound.exception;

import com.fourweekdays.fourweekdays.global.exception.BaseException;
import com.fourweekdays.fourweekdays.global.exception.ExceptionType;

public class OutboundException extends BaseException {
    public OutboundException(ExceptionType exceptionType) {
        super(exceptionType);
    }
}
