package com.ceos23.spring_boot.global.exception;

import lombok.Getter;

@Getter
public enum ErrorCode {

    // 공통
    INTERNAL_ERROR(500, "INTERNAL_ERROR", "서버 에러입니다"),

    // 잘못된 요청
    BAD_REQUEST(400, "BAD_REQUEST", "잘못된 요청입니다"),
    INVALID_INPUT_VALUE(400, "INVALID_INPUT_VALUE", "입력값이 올바르지 않습니다"),
    INVALID_PASSWORD(400, "INVALID_PASSWORD", "비밀번호가 올바르지 않습니다"),

    // 조회 실패 (404)
    USER_NOT_FOUND(404, "USER_NOT_FOUND", "유저를 찾을 수 없습니다"),
    MOVIE_NOT_FOUND(404, "MOVIE_NOT_FOUND", "영화를 찾을 수 없습니다"),
    THEATER_NOT_FOUND(404, "THEATER_NOT_FOUND", "영화관을 찾을 수 없습니다"),
    FAVORITE_THEATER_NOT_FOUND(404, "FAVORITE_THEATER_NOT_FOUND", "찜한 영화관 정보를 찾을 수 없습니다"),
    SCREENING_NOT_FOUND(404, "SCREENING_NOT_FOUND", "상영 정보를 찾을 수 없습니다"),
    SEAT_NOT_FOUND(404, "SEAT_NOT_FOUND", "좌석을 찾을 수 없습니다"),
    RESERVATION_NOT_FOUND(404, "RESERVATION_NOT_FOUND", "예약을 찾을 수 없습니다"),
    FAVORITE_MOVIE_NOT_FOUND(404, "FAVORITE_MOVIE_NOT_FOUND", "찜한 영화 정보를 찾을 수 없습니다"),
    FAVORITE_MOVIE_ALREADY_EXISTS(400, "FAVORITE_MOVIE_ALREADY_EXISTS", "이미 찜한 영화입니다"),
    INSUFFICIENT_STOCK(400, "INSUFFICIENT_STOCK", "재고가 부족합니다"),
    ITEM_NOT_FOUND(404, "ITEM_NOT_FOUND", "상품을 찾을 수 없습니다"),
    ITEM_STOCK_NOT_FOUND(404, "ITEM_STOCK_NOT_FOUND", "해당 영화관의 상품 재고를 찾을 수 없습니다"),
    ITEM_ORDER_NOT_FOUND(404, "ITEM_ORDER_NOT_FOUND", "주문 정보를 찾을 수 없습니다"),

    // 비즈니스 로직
    SEAT_ALREADY_RESERVED(400, "SEAT_ALREADY_RESERVED", "이미 예약된 좌석입니다"),
    FAVORITE_THEATER_ALREADY_EXISTS(400, "FAVORITE_THEATER_ALREADY_EXISTS", "이미 찜한 영화관입니다"),

    // 외부 결제 API
    PAYMENT_API_ERROR(500, "PAYMENT_API_ERROR", "결제 API 호출 중 오류가 발생했습니다"),
    PAYMENT_BAD_REQUEST(400, "PAYMENT_BAD_REQUEST", "결제 요청이 올바르지 않습니다"),
    PAYMENT_NOT_FOUND(404, "PAYMENT_NOT_FOUND", "결제 정보를 찾을 수 없습니다"),
    PAYMENT_SERVER_ERROR(502, "PAYMENT_SERVER_ERROR", "외부 결제 서버 오류가 발생했습니다");

    private final int status;
    private final String code;
    private final String message;

    ErrorCode(int status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }
}