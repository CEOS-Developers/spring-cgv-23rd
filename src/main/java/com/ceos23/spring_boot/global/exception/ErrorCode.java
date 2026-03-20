package com.ceos23.spring_boot.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    THEATER_NOT_FOUND(HttpStatus.NOT_FOUND, "T001", "존재하지 않는 영화관입니다."),
    DUPLICATE_THEATER_NAME(HttpStatus.BAD_REQUEST, "T002", "이미 존재하는 영화관 지점명입니다."),

    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "C001", "잘못된 입력값입니다."),

    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "S001", "서버 내부에 오류가 발생했습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
