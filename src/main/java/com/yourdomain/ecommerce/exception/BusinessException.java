package com.yourdomain.ecommerce.exception;

import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {

    private final ErrorCode errorCode;
    private final transient Object[] args;

    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getDefaultMessage());
        this.errorCode = errorCode;
        this.args = new Object[0];
    }

    public BusinessException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
        this.args = new Object[0];
    }

    public BusinessException(ErrorCode errorCode, String message, Object... args) {
        super(message);
        this.errorCode = errorCode;
        this.args = args;
    }
}
