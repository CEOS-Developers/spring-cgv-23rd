package com.ceos23.spring_boot.cgv.global.exception;

import jakarta.servlet.http.HttpServletRequest;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    protected ResponseEntity<ErrorResponse> handleNotFoundException(
            NotFoundException e,
            HttpServletRequest request
    ) {
        ErrorCode errorCode = e.getErrorCode();
        log.warn(
                "event=request_error code={} status={} path={} requestId={} message={}",
                errorCode.getCode(),
                errorCode.getHttpStatus().value(),
                request.getRequestURI(),
                MDC.get("requestId"),
                e.getMessage()
        );
        return ResponseEntity
                .status(errorCode.getHttpStatus())
                .body(ErrorResponse.of(errorCode, e.getMessage()));
    }

    @ExceptionHandler(BadRequestException.class)
    protected ResponseEntity<ErrorResponse> handleBadRequestException(
            BadRequestException e,
            HttpServletRequest request
    ) {
        ErrorCode errorCode = e.getErrorCode();
        log.warn(
                "event=request_error code={} status={} path={} requestId={} message={}",
                errorCode.getCode(),
                errorCode.getHttpStatus().value(),
                request.getRequestURI(),
                MDC.get("requestId"),
                e.getMessage()
        );
        return ResponseEntity
                .status(errorCode.getHttpStatus())
                .body(ErrorResponse.of(errorCode, e.getMessage()));
    }

    @ExceptionHandler(ConflictException.class)
    protected ResponseEntity<ErrorResponse> handleConflictException(
            ConflictException e,
            HttpServletRequest request
    ) {
        ErrorCode errorCode = e.getErrorCode();
        log.warn(
                "event=request_error code={} status={} path={} requestId={} message={}",
                errorCode.getCode(),
                errorCode.getHttpStatus().value(),
                request.getRequestURI(),
                MDC.get("requestId"),
                e.getMessage()
        );
        return ResponseEntity
                .status(errorCode.getHttpStatus())
                .body(ErrorResponse.of(errorCode, e.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException e,
            HttpServletRequest request
    ) {
        String message = e.getBindingResult().getFieldErrors().stream()
                .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
                .distinct()
                .collect(Collectors.joining(", "));

        if (message.isBlank()) {
            message = ErrorCode.BAD_REQUEST.getMessage();
        }

        log.warn(
                "event=request_validation_error code={} status={} path={} requestId={} message={}",
                ErrorCode.BAD_REQUEST.getCode(),
                ErrorCode.BAD_REQUEST.getHttpStatus().value(),
                request.getRequestURI(),
                MDC.get("requestId"),
                message
        );

        return ResponseEntity
                .status(ErrorCode.BAD_REQUEST.getHttpStatus())
                .body(ErrorResponse.of(ErrorCode.BAD_REQUEST, message));
    }

    @ExceptionHandler(Exception.class)
    protected ResponseEntity<ErrorResponse> handleException(Exception e, HttpServletRequest request) {
        log.error(
                "event=request_unhandled_error code={} status={} path={} requestId={}",
                ErrorCode.INTERNAL_SERVER_ERROR.getCode(),
                ErrorCode.INTERNAL_SERVER_ERROR.getHttpStatus().value(),
                request.getRequestURI(),
                MDC.get("requestId"),
                e
        );
        return ResponseEntity
                .status(ErrorCode.INTERNAL_SERVER_ERROR.getHttpStatus())
                .body(ErrorResponse.of(ErrorCode.INTERNAL_SERVER_ERROR, e.getMessage()));
    }
}
