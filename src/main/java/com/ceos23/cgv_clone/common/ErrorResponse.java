package com.ceos23.cgv_clone.common;

import com.ceos23.cgv_clone.common.codes.ErrorCode;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class ErrorResponse {

    private final String resultCode;

    private final String resultMsg;

    private final boolean isSuccess = false;

    public static ErrorResponse of(final ErrorCode errorCode) {
        return new ErrorResponse(errorCode.getDivisionCode(), errorCode.getMessage());
    }

    public static ErrorResponse of(final ErrorCode errorCode, final String reason) {
        return new ErrorResponse(errorCode.getDivisionCode(), reason);
    }
}
