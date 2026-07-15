package com.yourdomain.ecommerce.exception;

public class ResourceNotFoundException extends BusinessException {

    public ResourceNotFoundException(ErrorCode errorCode) {
        super(errorCode);
    }

    public ResourceNotFoundException(ErrorCode errorCode, String resource, Object id) {
        super(errorCode, "%s not found with id: %s".formatted(resource, id));
    }
}
