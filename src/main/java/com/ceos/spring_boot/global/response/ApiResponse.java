package com.ceos.spring_boot.global.response;

import com.ceos.spring_boot.global.codes.SuccessCode;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
public class ApiResponse<T> {

    // API 응답
    private T response;

    @Schema(description = "상태코드 ", example = "200")
    private int statusCode;

    @Schema(description = "상영 코드 메시지", example = "DELETE_SUCCESS")
    private String message;

    @Builder
    public ApiResponse(T response, int statusCode, String message) {
        this.response = response;
        this.statusCode = statusCode;
        this.message = message;
    }


    public static <T> ApiResponse<T> of(T response, SuccessCode successCode) {
        return ApiResponse.<T>builder()
                .response(response)
                .statusCode(successCode.getStatusCode())
                .message(successCode.getMessage())
                .build();
    }

}