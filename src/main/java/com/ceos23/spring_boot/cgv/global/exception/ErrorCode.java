package com.ceos23.spring_boot.cgv.global.exception;

import org.springframework.http.HttpStatus;

public enum ErrorCode {

    BAD_REQUEST(HttpStatus.BAD_REQUEST, "COMMON_400", "잘못된 요청입니다."),
    NOT_FOUND(HttpStatus.NOT_FOUND, "COMMON_404", "대상을 찾을 수 없습니다."),
    CONFLICT(HttpStatus.CONFLICT, "COMMON_409", "이미 처리되었거나 충돌이 발생했습니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON_500", "서버 내부 오류가 발생했습니다."),

    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "USER_404", "해당 사용자를 찾을 수 없습니다."),
    CINEMA_NOT_FOUND(HttpStatus.NOT_FOUND, "CINEMA_404", "해당 영화관을 찾을 수 없습니다."),
    MOVIE_NOT_FOUND(HttpStatus.NOT_FOUND, "MOVIE_404", "해당 영화를 찾을 수 없습니다."),
    SCREENING_NOT_FOUND(HttpStatus.NOT_FOUND, "SCREENING_404", "해당 상영 정보를 찾을 수 없습니다."),
    RESERVATION_NOT_FOUND(HttpStatus.NOT_FOUND, "RESERVATION_404", "해당 예매를 찾을 수 없습니다."),
    SEAT_TEMPLATE_NOT_FOUND(HttpStatus.NOT_FOUND, "SEAT_TEMPLATE_404", "존재하지 않는 좌석이 포함되어 있습니다."),
    STORE_MENU_STOCK_NOT_FOUND(HttpStatus.NOT_FOUND, "STORE_STOCK_404", "해당 영화관에 메뉴 재고 정보가 없습니다."),

    EMPTY_SEAT_REQUEST(HttpStatus.BAD_REQUEST, "RESERVATION_400", "예매할 좌석이 없습니다."),
    DUPLICATE_SEAT_REQUEST(HttpStatus.BAD_REQUEST, "RESERVATION_401", "중복된 좌석이 포함되어 있습니다."),
    INVALID_SEAT_FOR_SCREENING(HttpStatus.BAD_REQUEST, "RESERVATION_402", "해당 상영관에서 선택할 수 없는 좌석입니다."),
    ALREADY_RESERVED_SEAT(HttpStatus.CONFLICT, "RESERVATION_409", "이미 예매된 좌석입니다."),
    ALREADY_CANCELED_RESERVATION(HttpStatus.CONFLICT, "RESERVATION_410", "이미 취소된 예매입니다."),
    INSUFFICIENT_MENU_STOCK(HttpStatus.BAD_REQUEST, "STORE_STOCK_400", "구매 가능한 재고가 부족합니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    ErrorCode(HttpStatus httpStatus, String code, String message) {
        this.httpStatus = httpStatus;
        this.code = code;
        this.message = message;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
