package com.cgv.spring_boot.domain.movie.exception;

import com.cgv.spring_boot.global.common.code.BaseErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MovieErrorCode implements BaseErrorCode {
    MOVIE_NOT_FOUND(404, "해당 영화를 찾을 수 없습니다."),
    MOVIE_ALREADY_WISHED(400, "이미 찜한 영화입니다.");

    private final int status;
    private final String message;
}
