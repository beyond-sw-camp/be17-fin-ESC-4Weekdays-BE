package com.fourweekdays.fourweekdays.asn.exception;

import com.fourweekdays.fourweekdays.global.exception.BaseException;
import com.fourweekdays.fourweekdays.global.exception.ExceptionType;

public class AsnException extends BaseException {

    public AsnException(ExceptionType exceptionType) {
        super(exceptionType);
    }
}
