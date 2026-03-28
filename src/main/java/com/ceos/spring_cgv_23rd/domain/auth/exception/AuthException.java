package com.ceos.spring_cgv_23rd.domain.auth.exception;

import com.ceos.spring_cgv_23rd.global.apiPayload.code.BaseErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum AuthException implements BaseErrorCode {

    // 회원가입 에러
    DUPLICATE_LOGINID(HttpStatus.BAD_REQUEST, "SIGNUP_4001", "중복되는 아이디가 존재합니다."),
    DUPLICATE_EMAIL(HttpStatus.BAD_REQUEST, "SIGNUP_4002", "중복되는 이메일이 존재합니다."),
    DUPLICATE_PHONE(HttpStatus.BAD_REQUEST, "SIGNUP_4003", "중복되는 전화번호가 존재합니다."),
    DUPLICATE_NICKNAME(HttpStatus.BAD_REQUEST, "SIGNUP_4004", "중복되는 닉네임이 존재합니다."),
    UNDER_AGE(HttpStatus.BAD_REQUEST, "SIGNUP_4005", "만 14세 미만은 가입할 수 없습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
