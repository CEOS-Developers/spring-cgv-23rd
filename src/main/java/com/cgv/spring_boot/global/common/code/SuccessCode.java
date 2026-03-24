package com.cgv.spring_boot.global.common.code;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SuccessCode {
    SELECT_SUCCESS(200, "조회에 성공하였습니다."),
    INSERT_SUCCESS(201, "등록에 성공하였습니다."),
    DELETE_SUCCESS(200, "삭제에 성공하였습니다.");

    private final int status;
    private final String message;
}
