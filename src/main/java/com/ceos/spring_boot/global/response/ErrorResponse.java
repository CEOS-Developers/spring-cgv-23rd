package com.ceos.spring_boot.global.response;

import com.ceos.spring_boot.global.codes.ErrorCode;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ErrorResponse {

    @JsonProperty("status")
    private int statusCode;
    private String message;

    @Builder
    protected ErrorResponse(final ErrorCode errorCode){
        this.statusCode = errorCode.getStatusCode();
        this.message = errorCode.getMessage();
    }

    public static ErrorResponse fromErrorCode(final ErrorCode code) {
        return new ErrorResponse(code);
    }

    public static ErrorResponse of(final ErrorCode code, final String message) {
        ErrorResponse response = new ErrorResponse(code);
        response.message = message;
        return response;
    }
}