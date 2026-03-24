package com.ceos23.spring_boot.global.exception;

import com.ceos23.spring_boot.global.response.ErrorResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // 🔥 1. RuntimeException 처리
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleRuntimeException(RuntimeException e) {

        ErrorResponse response = ErrorResponse.builder()
                .status(ErrorCode.NOT_FOUND.getStatus())
                .code(ErrorCode.NOT_FOUND.getCode())
                .message(e.getMessage())
                .build();

        return ResponseEntity
                .status(ErrorCode.NOT_FOUND.getStatus())
                .body(response);
    }

    // 🔥 2. 모든 예외 처리
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception e) {

        ErrorResponse response = ErrorResponse.builder()
                .status(ErrorCode.INTERNAL_ERROR.getStatus())
                .code(ErrorCode.INTERNAL_ERROR.getCode())
                .message(e.getMessage())
                .build();

        return ResponseEntity
                .status(ErrorCode.INTERNAL_ERROR.getStatus())
                .body(response);
    }
}