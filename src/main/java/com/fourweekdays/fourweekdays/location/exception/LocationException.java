package com.fourweekdays.fourweekdays.location.exception;

import com.fourweekdays.fourweekdays.global.exception.BaseException;
import com.fourweekdays.fourweekdays.global.exception.ExceptionType;

public class LocationException extends BaseException {

    public LocationException(ExceptionType exceptionType) {
        super(exceptionType);
    }
}
