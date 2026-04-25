package com.ceos23.spring_cgv_23rd.global.Exception;

import lombok.Getter;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.AuthenticationException;

@Getter
public class CustomAuthenticationException extends AuthenticationException {
    private final ErrorCode code;

    public CustomAuthenticationException(ErrorCode errorCode) {
        super(errorCode.getErrorMessage());
        this.code = errorCode;
    }
}
