package com.ceos.spring_boot.global.response;

import com.ceos.spring_boot.global.codes.SuccessCode;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
public class ApiResponse<T> {

    // API 응답
    @Schema(description = "실제 응답 데이터")
    private T data;

    @Schema(description = "상태코드 ", example = "200")
    private int code;

    @Schema(description = "상영 코드 메시지", example = "GET_SUCCESS")
    private String message;

    @Builder
    public ApiResponse(T data, int code, String message) {
        this.data = data;
        this.code = code;
        this.message = message;
    }


    public static <T> ApiResponse<T> of(T data, SuccessCode successCode) {
        return ApiResponse.<T>builder()
                .data(data)
                .code(successCode.getStatusCode())
                .message(successCode.getMessage())
                .build();
    }

}