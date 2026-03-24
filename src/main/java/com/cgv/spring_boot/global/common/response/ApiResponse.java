package com.cgv.spring_boot.global.common.response;

import com.cgv.spring_boot.global.common.code.SuccessCode;

public record ApiResponse<T>(
        int status,
        String message,
        T data
) {
    // SuccessCode를 인자로 받는 정적 팩토리 메서드
    public static <T> ApiResponse<T> success(SuccessCode code, T data) {
        return new ApiResponse<>(code.getStatus(), code.getMessage(), data);
    }

    // 기본 성공 응답
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(200, "요청에 성공하였습니다.", data);
    }
}
