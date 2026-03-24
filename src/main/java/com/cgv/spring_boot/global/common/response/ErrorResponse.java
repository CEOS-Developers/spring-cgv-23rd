package com.cgv.spring_boot.global.common.response;

import lombok.Builder;

@Builder
public record ErrorResponse(
        int status,
        String message
) {
    public static ErrorResponse of(int status, String message) {
        return new ErrorResponse(status, message);
    }
}
