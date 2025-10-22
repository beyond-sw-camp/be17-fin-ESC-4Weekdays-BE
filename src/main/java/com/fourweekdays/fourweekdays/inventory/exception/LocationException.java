package com.fourweekdays.fourweekdays.inventory.exception;

import com.fourweekdays.fourweekdays.global.exception.BaseException;
import com.fourweekdays.fourweekdays.global.exception.ExceptionType;

public class LocationException extends BaseException {

    public LocationException(ExceptionType exceptionType) {
        super(exceptionType);
    }
}
