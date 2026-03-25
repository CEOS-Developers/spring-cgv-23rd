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
    FORBIDDEN_ACCESS(403, "해당 자원에 대한 권한이 없습니다."),
    // USER
    USER_NOT_FOUND(404, "해당 사용자를 찾을 수 없습니다."),
    LOGIN_ID_ALREADY_EXISTS(400, "이미 사용 중인 아이디입니다."),
    INVALID_LOGIN(401, "아이디 또는 비밀번호가 올바르지 않습니다."),
    INVALID_TOKEN(401, "유효하지 않은 토큰입니다."),
    EXPIRED_TOKEN(401, "만료된 토큰입니다."),
    // MOVIE
    MOVIE_NOT_FOUND(404, "해당 영화를 찾을 수 없습니다."),
    MOVIE_ALREADY_WISHED(400, "이미 찜한 영화입니다."),
    // THEATER
    THEATER_NOT_FOUND(404, "해당 영화관을 찾을 수 없습니다."),
    THEATER_ALREADY_WISHED(400, "이미 찜한 영화관입니다."),
    // SCHEDULE
    SCHEDULE_NOT_FOUND(404, "해당 영화 스케줄을 찾을 수 없습니다."),
    // RESERVATION
    RESERVATION_NOT_FOUND(404, "해당 예매 내역을 찾을 수 없습니다."),
    ALREADY_RESERVED_SEAT(400, "이미 예약된 좌석입니다."),
    ALREADY_CANCELED(400, "이미 취소된 예매입니다."),
    // STORE
    INVALID_STOCK_QUANTITY(400, "재고 수량은 1개 이상이어야 합니다.");

    private final int status;
    private final String message;
}
