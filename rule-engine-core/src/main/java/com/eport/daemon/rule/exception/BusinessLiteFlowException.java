package com.eport.daemon.rule.exception;

import com.yomahub.liteflow.exception.LiteFlowException;

public class BusinessLiteFlowException extends LiteFlowException {
    public BusinessLiteFlowException(String code, String message) {
        super(code, message);
    }
}
