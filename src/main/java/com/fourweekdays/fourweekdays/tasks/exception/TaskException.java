package com.fourweekdays.fourweekdays.tasks.exception;

import com.fourweekdays.fourweekdays.global.exception.BaseException;
import com.fourweekdays.fourweekdays.global.exception.ExceptionType;

public class TaskException extends BaseException {

    public TaskException(ExceptionType exceptionType) {
        super(exceptionType);
    }
}
