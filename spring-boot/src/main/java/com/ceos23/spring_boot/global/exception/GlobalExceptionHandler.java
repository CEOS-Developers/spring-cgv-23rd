package com.ceos23.spring_boot.global.exception;

import com.ceos23.spring_boot.exception.CustomException;
import com.ceos23.spring_boot.global.response.ErrorResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // 1. CustomException 처리
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ErrorResponse> handleCustomException(CustomException e) {

        ErrorCode errorCode = e.getErrorCode();

        ErrorResponse response = ErrorResponse.builder()
                .status(errorCode.getStatus())
                .code(errorCode.getCode())
                .message(errorCode.getMessage())
                .build();

        return ResponseEntity
                .status(errorCode.getStatus())
                .body(response);
    }

    // 2. 잘못된 인자값 처리
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException e) {

        ErrorResponse response = ErrorResponse.builder()
                .status(ErrorCode.BAD_REQUEST.getStatus())
                .code(ErrorCode.BAD_REQUEST.getCode())
                .message(e.getMessage())
                .build();

        return ResponseEntity
                .status(ErrorCode.BAD_REQUEST.getStatus())
                .body(response);
    }

    // 3. 나머지 예외 처리
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