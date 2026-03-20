package com.ceos.spring_cgv_23rd.domain.screening.exception;

import com.ceos.spring_cgv_23rd.global.apiPayload.code.BaseErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ScreeningErrorCode implements BaseErrorCode {
    
    SCREENING_NOT_FOUND(HttpStatus.NOT_FOUND, "SCREENING_4041", "존재하지 않는 상영 스케줄입니다.");


    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}