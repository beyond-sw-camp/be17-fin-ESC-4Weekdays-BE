package com.fourweekdays.fourweekdays.purchaseorder.exception;

import com.fourweekdays.fourweekdays.global.exception.BaseException;
import com.fourweekdays.fourweekdays.global.exception.ExceptionType;

public class PurchaseOrderException extends BaseException {

    public PurchaseOrderException(ExceptionType exceptionType) {
        super(exceptionType);
    }
}
