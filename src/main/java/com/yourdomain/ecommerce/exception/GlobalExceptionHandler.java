package com.yourdomain.ecommerce.exception;

import com.yourdomain.ecommerce.common.ApiResponse;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<Object>> handleBusiness(BusinessException ex) {
        ErrorCode code = ex.getErrorCode();
        log.warn("Business exception [{}]: {}", code.getCode(), ex.getMessage());
        return ResponseEntity.status(code.getHttpStatus())
                .body(ApiResponse.error(code.getCode(), ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> errors = ex.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.toMap(
                        f -> f.getField(),
                        f -> f.getDefaultMessage() == null ? "invalid" : f.getDefaultMessage(),
                        (a, b) -> a));
        ApiResponse<Map<String, String>> body = ApiResponse.<Map<String, String>>builder()
                .success(false)
                .errorCode(ErrorCode.VALIDATION_FAILED.getCode())
                .message(ErrorCode.VALIDATION_FAILED.getDefaultMessage())
                .data(errors)
                .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleConstraint(ConstraintViolationException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getConstraintViolations().forEach(v ->
                errors.put(v.getPropertyPath().toString(), v.getMessage()));
        ApiResponse<Map<String, String>> body = ApiResponse.<Map<String, String>>builder()
                .success(false)
                .errorCode(ErrorCode.VALIDATION_FAILED.getCode())
                .message(ErrorCode.VALIDATION_FAILED.getDefaultMessage())
                .data(errors)
                .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler({HttpMessageNotReadableException.class,
            MissingServletRequestParameterException.class,
            MethodArgumentTypeMismatchException.class})
    public ResponseEntity<ApiResponse<Object>> handleBadRequest(Exception ex) {
        log.warn("Bad request: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(ErrorCode.BAD_REQUEST.getCode(), ex.getMessage()));
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiResponse<Object>> handleMethodNotSupported(HttpRequestMethodNotSupportedException ex) {
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED)
                .body(ApiResponse.error(ErrorCode.METHOD_NOT_ALLOWED.getCode(), ex.getMessage()));
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleNotFound(NoHandlerFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(ErrorCode.RESOURCE_NOT_FOUND.getCode(), ex.getMessage()));
    }

    @ExceptionHandler({AuthenticationException.class, BadCredentialsException.class})
    public ResponseEntity<ApiResponse<Object>> handleAuth(AuthenticationException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.error(ErrorCode.UNAUTHORIZED.getCode(), ex.getMessage()));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<Object>> handleAccessDenied(AccessDeniedException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(ApiResponse.error(ErrorCode.FORBIDDEN.getCode(), ex.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleUnknown(Exception ex) {
        log.error("Unhandled exception", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error(ErrorCode.INTERNAL_ERROR.getCode(), ErrorCode.INTERNAL_ERROR.getDefaultMessage()));
    }
}
