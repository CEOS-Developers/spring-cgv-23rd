package com.ceos23.spring_boot.global.exception;

import lombok.Getter;

@Getter
public enum ErrorCode {

    NOT_FOUND(404, "NOT_FOUND", "데이터를 찾을 수 없습니다"),
    BAD_REQUEST(400, "BAD_REQUEST", "잘못된 요청입니다"),
    INTERNAL_ERROR(500, "INTERNAL_ERROR", "서버 에러입니다"),

    SEAT_ALREADY_RESERVED(400, "SEAT_ALREADY_RESERVED", "이미 예약된 좌석입니다");

    private final int status;
    private final String code;
    private final String message;

    ErrorCode(int status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }
}