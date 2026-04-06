package com.ceos23.spring_boot.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    THEATER_NOT_FOUND(HttpStatus.NOT_FOUND, "T001", "존재하지 않는 영화관입니다."),
    DUPLICATE_THEATER_NAME(HttpStatus.BAD_REQUEST, "T002", "이미 존재하는 영화관 지점명입니다."),

    MOVIE_NOT_FOUND(HttpStatus.BAD_REQUEST, "M001", "존재하지 않는 영화입니다."),
    DUPLICATE_MOVIE(HttpStatus.BAD_REQUEST, "M002", "이미 존재하는 영화입니다."),

    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "U001", "존재하지 않는 회원입니다."),
    DUPLICATE_EMAIL(HttpStatus.CONFLICT, "U002", "이미 사용 중인 이메일입니다."),
    INVALID_PASSWORD(HttpStatus.UNAUTHORIZED, "U003", "비밀번호가 틀렸습니다."),

    SCHEDULE_NOT_FOUND(HttpStatus.NOT_FOUND, "SC001", "존재하지 않는 상영 일정입니다."),
    SCHEDULE_ALREADY_STARTED(HttpStatus.BAD_REQUEST, "S002", "이미 상영이 시작되었거나 종료된 일정입니다."),
    CANCELLATION_DEADLINE_PASSED(HttpStatus.BAD_REQUEST, "S003", "예매 취소 가능 시간이 지났습니다."),


    SEAT_NOT_FOUND(HttpStatus.NOT_FOUND, "SE001", "존재하지 않는 좌석입니다."),
    INVALID_SEAT(HttpStatus.BAD_REQUEST, "SE002", "사용 불가능한 좌석입니다."),

    SEAT_ALREADY_RESERVED(HttpStatus.CONFLICT, "R001", "이미 예매가 완료된 좌석입니다."),
    RESERVATION_NOT_FOUND(HttpStatus.NOT_FOUND, "R002", "존재하지 않는 예매 내역입니다."),
    UNAUTHORIZED_RESERVATION_ACCESS(HttpStatus.FORBIDDEN, "R003", "본인의 예매 내역만 접근할 수 있습니다."),
    ALREADY_CANCELED_RESERVATION(HttpStatus.BAD_REQUEST, "R004", "이미 취소된 예매입니다."),

    MENU_NOT_FOUND(HttpStatus.NOT_FOUND, "ME001", "존재하지 않는 메뉴입니다."),

    INVENTORY_NOT_FOUND(HttpStatus.NOT_FOUND, "I001", "존재하지 않는 재고입니다."),

    OUT_OF_STOCK(HttpStatus.BAD_REQUEST, "ST001", "재고는 항상 1 이상이어야 합니다."),

    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "C001", "잘못된 입력값입니다."),

    UNAUTHORIZED_USER(HttpStatus.UNAUTHORIZED, "SC001", "인증이 필요합니다."),
    ACCESS_DENIED(HttpStatus.FORBIDDEN, "SC002", "해당 요청에 권한이 없습니다."),

    MISSING_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "CK001", "쿠키에 refreshToken이 없습니다."),
    INVALID_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "CK002", "유효하지 않거나 만료된 refreshToken입니다. "),

    LOCK_ACQUISITION_FAILED(HttpStatus.CONFLICT, "L001", "현재 다른 사용자가 처리 중입니다. 잠시 후 다시 시도해주세요."),
    LOCK_INTERRUPTED_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "L002", "시스템 처리 중 인터럽트가 발생했습니다."),

    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "S001", "서버 내부에 오류가 발생했습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
