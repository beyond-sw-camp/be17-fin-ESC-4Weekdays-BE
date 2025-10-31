package com.fourweekdays.fourweekdays.franchise.exception;

import com.fourweekdays.fourweekdays.global.exception.BaseException;
import com.fourweekdays.fourweekdays.global.exception.ExceptionType;

public class FranchiseException extends BaseException {

    public FranchiseException(ExceptionType exceptionType) {
        super(exceptionType);
    }
}
