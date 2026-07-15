package com.yourdomain.ecommerce.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

    // Generic
    INTERNAL_ERROR("ERR_0000", "Internal server error", HttpStatus.INTERNAL_SERVER_ERROR),
    VALIDATION_FAILED("ERR_0001", "Validation failed", HttpStatus.BAD_REQUEST),
    BAD_REQUEST("ERR_0002", "Bad request", HttpStatus.BAD_REQUEST),
    METHOD_NOT_ALLOWED("ERR_0003", "Method not allowed", HttpStatus.METHOD_NOT_ALLOWED),
    RESOURCE_NOT_FOUND("ERR_0004", "Resource not found", HttpStatus.NOT_FOUND),
    CONFLICT("ERR_0005", "Resource conflict", HttpStatus.CONFLICT),

    // Auth
    UNAUTHORIZED("ERR_0100", "Unauthorized", HttpStatus.UNAUTHORIZED),
    FORBIDDEN("ERR_0101", "Forbidden", HttpStatus.FORBIDDEN),
    INVALID_CREDENTIALS("ERR_0102", "Invalid credentials", HttpStatus.UNAUTHORIZED),
    INVALID_TOKEN("ERR_0103", "Invalid or expired token", HttpStatus.UNAUTHORIZED),

    // User
    USER_NOT_FOUND("ERR_1000", "User not found", HttpStatus.NOT_FOUND),
    USER_ALREADY_EXISTS("ERR_1001", "User already exists", HttpStatus.CONFLICT),

    // Product / Order
    PRODUCT_NOT_FOUND("ERR_2000", "Product not found", HttpStatus.NOT_FOUND),
    PRODUCT_OUT_OF_STOCK("ERR_2001", "Product out of stock", HttpStatus.CONFLICT),
    CATEGORY_NOT_FOUND("ERR_2002", "Category not found", HttpStatus.NOT_FOUND),
    ORDER_NOT_FOUND("ERR_3000", "Order not found", HttpStatus.NOT_FOUND),
    ORDER_INVALID_STATE("ERR_3001", "Order state transition not allowed", HttpStatus.CONFLICT),
    EMPTY_CART("ERR_3002", "Cart is empty", HttpStatus.CONFLICT),

    // Cart
    CART_NOT_FOUND("ERR_4000", "Cart not found", HttpStatus.NOT_FOUND),
    CART_ITEM_NOT_FOUND("ERR_4001", "Cart item not found", HttpStatus.NOT_FOUND),

    // Password reset
    INVALID_RESET_TOKEN("ERR_5000", "Invalid or expired password reset token", HttpStatus.BAD_REQUEST);

    private final String code;
    private final String defaultMessage;
    private final HttpStatus httpStatus;

    ErrorCode(String code, String defaultMessage, HttpStatus httpStatus) {
        this.code = code;
        this.defaultMessage = defaultMessage;
        this.httpStatus = httpStatus;
    }
}
