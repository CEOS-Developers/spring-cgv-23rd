package com.ceos.spring_boot.global.codes;

import lombok.Getter;

@Getter
public enum SuccessCode {

    // 조회 성공
    GET_SUCCESS(200, "GET_SUCCESS"),

    // 로그인 성공
    LOGIN_SUCCESS(200, "LOGIN_SUCCESS"),

    // 삭제 성공
    DELETE_SUCCESS(200, "DELETE_SUCCESS"),

    // 삽입 성공
    INSERT_SUCCESS(201, "INSERT_SUCCESS"),

    // 수정 성공
    UPDATE_SUCCESS(204, "UPDATE_SUCCESS"),

    // 결제 성공
    PAYMENT_SUCCESS(200, "PAYMENT_SUCCESS"),

    // 결제 취소 성공
    CANCEL_SUCCESS(200, "CANCEL_SUCCESS"),

    // 결제 조회 성공
    GET_PAYMENT_SUCCESS(200, "GET_PAYMENT_SUCCESS"),

    ;

    private final int statusCode;

    private final String message;

    SuccessCode(final int statusCode, final String message) {
        this.statusCode = statusCode;
        this.message = message;
    }
}
