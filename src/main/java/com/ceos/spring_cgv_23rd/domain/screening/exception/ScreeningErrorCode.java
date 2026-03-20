package com.ceos.spring_cgv_23rd.domain.screening.exception;

import com.ceos.spring_cgv_23rd.global.apiPayload.code.BaseErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ScreeningErrorCode implements BaseErrorCode {

    SCREENING_NOT_FOUND(HttpStatus.NOT_FOUND, "SCREENING_4041", "존재하지 않는 상영 스케줄입니다."),
    NO_REMAINING_SEATS(HttpStatus.BAD_REQUEST, "SCREENING_4002", "남은 좌석이 부족합니다."),
    INVALID_SEAT_COUNT(HttpStatus.BAD_REQUEST, "SCREENING_4003", "좌석 수가 올바르지 않습니다.");


    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}