package com.ceos23.cgv_clone.common;

import com.ceos23.cgv_clone.common.codes.ErrorCode;
import com.ceos23.cgv_clone.common.codes.SuccessCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * [공통] API Response 결과의 반환 값을 관리
 */
@Getter
@AllArgsConstructor
public class ApiResponse<T> {

    // API 응답 결과 Response
    private T result;

    // API 응답 코드 Response
    private int resultCode;

    // API 응답 코드 Message
    private String resultMsg;

    private boolean isSuccess;

    private Object error;

    public static <T> ApiResponse<T> ok(SuccessCode successCode, T result) {
        return new ApiResponse<>(result, successCode.getHttpStatus().value(), successCode.getMessage(), true, null);
    }

    public static <T> ApiResponse<T> ok(SuccessCode successCode) {
        return new ApiResponse<>(null, successCode.getHttpStatus().value(), successCode.getMessage(), true, null);
    }

    public static <T> ApiResponse<T> fail(ErrorCode errorCode, Object error) {
        return new ApiResponse<>(null, errorCode.getHttpStatus().value(), errorCode.getMessage(), false, error);
    }

}