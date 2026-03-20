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
    // USER
    USER_NOT_FOUND(404, "해당 사용자를 찾을 수 없습니다."),
    // MOVIE
    MOVIE_NOT_FOUND(404, "해당 영화를 찾을 수 없습니다."),
    // THEATER
    THEATER_NOT_FOUND(404, "해당 영화관을 찾을 수 없습니다."),
    // SCHEDULE
    SCHEDULE_NOT_FOUND(404, "해당 영화 스케줄을 찾을 수 없습니다."),
    // RESERVATION
    RESERVATION_NOT_FOUND(404, "해당 예매 내역을 찾을 수 없습니다."),
    ALREADY_RESERVED_SEAT(400, "이미 예약된 좌석입니다."),
    ALREADY_CANCELED(400, "이미 취소된 예매입니다.");

    private final int status;
    private final String message;
}
