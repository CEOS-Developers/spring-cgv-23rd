package com.cgv.spring_boot.global.error.code;

import com.cgv.spring_boot.global.common.code.BaseErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum GlobalErrorCode implements BaseErrorCode {
    INVALID_INPUT_VALUE(400, "잘못된 입력 값입니다."),
    UNAUTHORIZED(401, "인증이 필요합니다."),
    METHOD_NOT_ALLOWED(405, "허용되지 않은 메서드입니다."),
    HANDLE_ACCESS_DENIED(403, "접근 권한이 없습니다."),
    INTERNAL_SERVER_ERROR(500, "서버 내부 오류가 발생했습니다."),
    FORBIDDEN_ACCESS(403, "해당 자원에 대한 권한이 없습니다.");

    private final int status;
    private final String message;
}
