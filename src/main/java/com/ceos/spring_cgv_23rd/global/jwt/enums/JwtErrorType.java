package com.ceos.spring_cgv_23rd.global.jwt.enums;

import com.ceos.spring_cgv_23rd.global.apiPayload.code.GeneralErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum JwtErrorType {
    TOKEN_EXPIRED(GeneralErrorCode.TOKEN_EXPIRED),
    INVALID_TOKEN(GeneralErrorCode.INVALID_TOKEN);

    private final GeneralErrorCode errorCode;
}
