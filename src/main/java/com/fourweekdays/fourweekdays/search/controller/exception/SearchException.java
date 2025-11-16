package com.fourweekdays.fourweekdays.search.controller.exception;

import com.fourweekdays.fourweekdays.global.exception.BaseException;
import com.fourweekdays.fourweekdays.global.exception.ExceptionType;

public class SearchException extends BaseException {

    public SearchException(ExceptionType exceptionType) {
        super(exceptionType);
    }
}
