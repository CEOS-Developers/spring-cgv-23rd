package com.ceos.spring_cgv_23rd.domain.movie.exception;

import com.ceos.spring_cgv_23rd.global.apiPayload.code.BaseErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum MovieErrorCode implements BaseErrorCode {

    // 영화 에러
    MOVIE_NOT_FOUND(HttpStatus.NOT_FOUND, "MOVIE_4041", "존재하지 않는 영화입니다."),

    // 영화 통계 에러
    MOVIE_STAT_NOT_FOUND(HttpStatus.NOT_FOUND, "MOVIE_4042", "영화 통계가 존재하지 않습니다.");


    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
