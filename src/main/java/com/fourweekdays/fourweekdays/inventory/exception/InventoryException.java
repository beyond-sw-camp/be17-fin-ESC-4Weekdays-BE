package com.fourweekdays.fourweekdays.inventory.exception;

import com.fourweekdays.fourweekdays.global.exception.BaseException;
import com.fourweekdays.fourweekdays.global.exception.ExceptionType;

public class InventoryException extends BaseException {

    public InventoryException(ExceptionType exceptionType) {
        super(exceptionType);
    }
}
