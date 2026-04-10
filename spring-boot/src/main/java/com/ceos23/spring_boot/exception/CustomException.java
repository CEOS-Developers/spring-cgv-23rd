package com.ceos23.spring_boot.exception;

import com.ceos23.spring_boot.global.exception.ErrorCode;

public class CustomException extends RuntimeException {

    private final ErrorCode errorCode;

    public CustomException(ErrorCode errorCode) {
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }
}