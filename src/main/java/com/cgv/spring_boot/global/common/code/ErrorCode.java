package com.cgv.spring_boot.global.common.code;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    // 공통 에러
    INVALID_INPUT_VALUE(400, "잘못된 입력 값입니다."),
    METHOD_NOT_ALLOWED(405, "허용되지 않은 메서드입니다."),
    HANDLE_ACCESS_DENIED(403, "접근 권한이 없습니다."),
    INTERNAL_SERVER_ERROR(500, "서버 내부 오류가 발생했습니다."),
    // User
    USER_NOT_FOUND(404, "해당 사용자를 찾을 수 없습니다."),
    // Movie
    MOVIE_NOT_FOUND(404, "해당 영화를 찾을 수 없습니다."),
    // Theater
    THEATER_NOT_FOUND(404, "해당 영화관을 찾을 수 없습니다."),
    // SCHEDULE
    SCHEDULE_NOT_FOUND(404, "해당 영화 스케줄을 찾을 수 없습니다.");

    private final int status;
    private final String message;
}
