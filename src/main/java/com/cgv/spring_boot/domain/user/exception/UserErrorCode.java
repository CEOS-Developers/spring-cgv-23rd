package com.cgv.spring_boot.domain.user.exception;

import com.cgv.spring_boot.global.common.code.BaseErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserErrorCode implements BaseErrorCode {
    USER_NOT_FOUND(404, "해당 사용자를 찾을 수 없습니다."),
    LOGIN_ID_ALREADY_EXISTS(400, "이미 사용 중인 아이디입니다."),
    INVALID_LOGIN(401, "아이디 또는 비밀번호가 올바르지 않습니다."),
    INVALID_TOKEN(401, "유효하지 않은 토큰입니다."),
    EXPIRED_TOKEN(401, "만료된 토큰입니다.");

    private final int status;
    private final String message;
}
