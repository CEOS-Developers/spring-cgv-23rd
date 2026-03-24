package com.cgv.spring_boot.global.common.response;

public record ApiResponse<T>(
        int status,
        String message,
        T data
) {
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(200, "성공", data);
    }
}
