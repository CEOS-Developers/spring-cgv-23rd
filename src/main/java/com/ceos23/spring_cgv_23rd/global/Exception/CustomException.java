package com.ceos23.spring_cgv_23rd.global.Exception;

import lombok.Getter;

@Getter
public class CustomException extends RuntimeException {
    private final ErrorCode code;

    public CustomException(ErrorCode code){
        super(code.getErrorMessage());
        this.code = code;
    }
}
