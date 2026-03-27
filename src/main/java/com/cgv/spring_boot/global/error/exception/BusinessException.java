package com.cgv.spring_boot.global.error.exception;

import com.cgv.spring_boot.global.common.code.BaseErrorCode;
import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {
    private final BaseErrorCode errorCode;

    public BusinessException(BaseErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
