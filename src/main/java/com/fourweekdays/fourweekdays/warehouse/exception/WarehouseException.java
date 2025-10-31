package com.fourweekdays.fourweekdays.warehouse.exception;

import com.fourweekdays.fourweekdays.global.exception.BaseException;
import com.fourweekdays.fourweekdays.global.exception.ExceptionType;

public class WarehouseException extends BaseException {

    public WarehouseException(ExceptionType exceptionType) {
        super(exceptionType);
    }
}
