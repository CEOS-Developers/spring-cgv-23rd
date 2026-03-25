package com.ceos23.cgv_clone.global.response;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum SuccessCode {

    /**
     * ******************************* Success CodeList ***************************************
     */
    // 조회 성공 코드 (HTTP Response: 200 OK)
    SELECT_SUCCESS(HttpStatus.OK, "200", "SELECT SUCCESS"),

    // 삭제 성공 코드 (HTTP Response: 200 OK)
    DELETE_SUCCESS(HttpStatus.OK, "200", "DELETE SUCCESS"),

    // 삽입 성공 코드 (HTTP Response: 201 Created)
    INSERT_SUCCESS(HttpStatus.CREATED, "201", "INSERT SUCCESS"),

    // 수정 성공 코드 (HTTP Response: 201 Created)
    UPDATE_SUCCESS(HttpStatus.NO_CONTENT, "204", "UPDATE SUCCESS"),

    ;

    /**
     * ******************************* Success Code Constructor ***************************************
     */
    // 성공 코드의 'HTTP 상태'를 반환한다.
    private final HttpStatus httpStatus;

    // 성공 코드의 '코드 값'을 반환한다.
    private final String code;

    // 성공 코드의 '코드 메시지'를 반환한다.
    private final String message;

    // 생성자 구성
    SuccessCode(final HttpStatus httpStatus, final String code, final String message) {
        this.httpStatus = httpStatus;
        this.code = code;
        this.message = message;
    }
}