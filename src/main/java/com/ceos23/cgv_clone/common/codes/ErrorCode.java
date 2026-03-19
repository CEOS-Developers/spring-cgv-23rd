package com.ceos23.cgv_clone.common.codes;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    /**
     ******************************** Global Error CodeList ****************************************
     * HTTP Status Code
     * 400 : Bad Request
     * 401 : Unauthorized
     * 403 : Forbidden
     * 404 : Not Found
     * 500 : Internal Server Error
     **********************************************************************************************
     */

    // 잘못된 서버 요청
    BAD_REQUEST_ERROR(HttpStatus.BAD_REQUEST, "G001", "Bad Request Exception"),

    // @RequestBody 데이터 미존재
    REQUEST_BODY_MISSING_ERROR(HttpStatus.BAD_REQUEST, "G002", "Required request body is missing"),

    // 유효하지 않은 타입
    INVALID_TYPE_VALUE(HttpStatus.BAD_REQUEST, "G003", " Invalid Type Value"),

    // Request Parameter 로 데이터가 전달되지 않을 경우
    MISSING_REQUEST_PARAMETER_ERROR(HttpStatus.BAD_REQUEST, "G004", "Missing Servlet RequestParameter Exception"),

    // 권한이 없음
    FORBIDDEN_ERROR(HttpStatus.FORBIDDEN, "G005", "Forbidden Exception"),

    // 서버로 요청한 리소스가 존재하지 않음
    NOT_FOUND_ERROR(HttpStatus.NOT_FOUND, "G006", "Not Found Exception"),

    // @RequestBody 및 @RequestParam, @PathVariable 값이 유효하지 않음
    NOT_VALID_ERROR(HttpStatus.BAD_REQUEST, "G007", "handle Validation Exception"),

    // 서버가 처리 할 방법을 모르는 경우 발생
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "G999", "Internal Server Error Exception"),

    /**
     ******************************** Custom Error CodeList ****************************************
     */

    // 유저 데이터 미존재
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "U001", "해당 유저를 찾을 수 없습니다."),

    // 영화관 데이터 미존재
    THEATER_NOT_FOUND(HttpStatus.NOT_FOUND, "T001", "해당 영화관을 찾을 수 없습니다."),

    // 영화관 찜 최대 5개 초과
    FAVORITE_THEATER_LIMIT_EXCEEDED(HttpStatus.BAD_REQUEST, "T002", "자주 가는 CGV는 최대 5개까지만 등록 가능합니다."),

    // 영화 데이터 미존재
    MOVIE_NOT_FOUND(HttpStatus.NOT_FOUND, "M001", "해당 영화를 찾을 수 없습니다."),

    // 해당 시간대 영화 미존재
    SCHEDULE_NOT_FOUND(HttpStatus.NOT_FOUND, "S001", "해당 시간대 영화를 찾을 수 없습니다."),

    // 관람 연령에 맞지 않음
    AGE_RESTRICTED(HttpStatus.BAD_REQUEST, "R001", "관람 등급이 맞지 않습니다."),

    // 해당 좌석 이미 예약됨
    ALREADY_RESERVED_SEAT(HttpStatus.BAD_REQUEST, "R002", "해당 좌석은 이미 예약되어 있습니다."),

    // 해당 예매 번호 미존재
    RESERVATION_NOT_FOUND(HttpStatus.NOT_FOUND, "R003", "해당 예매 번호를 찾을 수 없습니다."),

    // 이미 취소된 예약
    ALREADY_CANCELED_RESERVATION(HttpStatus.BAD_REQUEST, "R004", "해당 예매는 이미 취소되었습니다.")
    ;

    /**
     ******************************** Error Code Constructor ****************************************
     */
    // 에러 코드의 'HTTP 상태'을 반환한다.
    private final HttpStatus httpStatus;

    // 에러 코드의 '코드간 구분 값'을 반환한다.
    private final String divisionCode;

    // 에러 코드의 '코드 메시지'을 반환한다.
    private final String message;

    // 생성자 구성
    ErrorCode(final HttpStatus httpStatus, final String divisionCode, final String message) {
        this.httpStatus = httpStatus;
        this.divisionCode = divisionCode;
        this.message = message;
    }
}
