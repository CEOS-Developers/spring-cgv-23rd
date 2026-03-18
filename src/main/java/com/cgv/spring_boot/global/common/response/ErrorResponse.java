package com.cgv.spring_boot.global.common.response;

import lombok.Builder;

@Builder
public record ErrorResponse(
        int status,
        String code,
        String message
) {
    public static ErrorResponse of(int status, String code, String message) {
        return new ErrorResponse(status, code, message);
    }
}
