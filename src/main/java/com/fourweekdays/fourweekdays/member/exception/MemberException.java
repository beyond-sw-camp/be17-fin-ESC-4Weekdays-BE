package com.fourweekdays.fourweekdays.member.exception;

import com.fourweekdays.fourweekdays.global.exception.BaseException;
import com.fourweekdays.fourweekdays.global.exception.ExceptionType;

public class MemberException extends BaseException {

    public MemberException(ExceptionType exceptionType) {
        super(exceptionType);
    }
}
