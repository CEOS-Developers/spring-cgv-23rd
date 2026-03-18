package com.cgv.spring_boot.global.common.code;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    // 공통 에러
    INVALID_INPUT_VALUE(400, "COMMON_001", "잘못된 입력 값입니다."),
    METHOD_NOT_ALLOWED(405, "COMMON_002", "허용되지 않은 메서드입니다."),
    HANDLE_ACCESS_DENIED(403, "COMMON_003", "접근 권한이 없습니다."),

    // 영화 관련
    MOVIE_NOT_FOUND(404, "MOVIE_001", "해당 영화를 찾을 수 없습니다.");

    private final int status;
    private final String code;
    private final String message;
}
