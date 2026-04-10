package com.cgv.spring_boot.domain.theater.exception;

import com.cgv.spring_boot.global.common.code.BaseErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TheaterErrorCode implements BaseErrorCode {
    THEATER_NOT_FOUND(404, "해당 영화관을 찾을 수 없습니다."),
    THEATER_ALREADY_WISHED(400, "이미 찜한 영화관입니다.");

    private final int status;
    private final String message;
}
