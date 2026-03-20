package com.ceos.spring_cgv_23rd.domain.theater.exception;

import com.ceos.spring_cgv_23rd.global.apiPayload.code.BaseErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum TheaterErrorCode implements BaseErrorCode {

    THEATER_NOT_FOUND(HttpStatus.NOT_FOUND, "THEATER_4041", "존재하지 않는 영화관입니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
