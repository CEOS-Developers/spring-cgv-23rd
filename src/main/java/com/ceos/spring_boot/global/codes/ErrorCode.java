package com.ceos.spring_boot.global.codes;

import lombok.Getter;

@Getter
public enum ErrorCode {

    // 잘못된 서버 요청
    BAD_REQUEST_ERROR(400,  "Bad Request Exception"),

    // @RequestBody 및 @RequestParam, @PathVariable 값이 유효하지 않음
    NOT_VALID_ERROR(400, "handle Validation Exception"),

    // @RequestBody 데이터 미 존재
    REQUEST_BODY_MISSING_ERROR(400,"Required request body is missing"),

    // Request Header 가 누락된 경우
    MISSING_REQUEST_HEADER_ERROR(400, "Missing Request Header Exception"),

    // 중복 닉네임 존재
    DUPLICATE_NICKNAME_ERROR(400, "Nickname already exist"),

    // 유효하지 않은 타입
    INVALID_TYPE_VALUE(400, " Invalid Type Value"),

    // Request Parameter 로 데이터가 전달되지 않을 경우
    MISSING_REQUEST_PARAMETER_ERROR(400,  "Missing Servlet RequestParameter Exception"),

    // Request Parameter가 Valid 하지 않은 경우
    INVALID_PARAMETER_ERROR(400,  "Invalid RequestParameter Exception"),

    // 인증 실패
    UNAUTHORIZED_ERROR(401,  "Unauthorized Exception"),

    // 권한이 없음
    FORBIDDEN_ERROR(403,  "Forbidden Exception"),

    // handler 존재 하지 않음
    NOT_FOUND_ERROR(404,  "Not Found Exception"),

    // 잘못된 경로로의 요청
    NO_RESOURCE_FOUND_ERROR(404, "No Resource Found Exception"),

    // 지원하지 않는 HTTP Method
    METHOD_NOT_ALLOWED_ERROR(405,  "Method Not Allowed"),

    // 기존 데이터와 충돌
    CONFLICT_ERROR(409, "Conflict Exception"),

    // CUSTUM_ERROR
    // 사용자 관련 에러
    USER_NOT_FOUND_ERROR(404,  "사용자를 찾을 수 없습니다."),
    DUPLICATE_EMAIL_ERROR(409, "이미 가입된 이메일입니다."),
    INVALID_LOGIN_ERROR(401, "비밀번호가 일치하지 않습니다."),

    // 영화관 관련 에러
    CINEMA_NOT_FOUND_ERROR(404, "해당 ID의 영화관을 찾을 수 없습니다."),

    // 영화 관련 에러
    MOVIE_NOT_FOUND_ERROR(404, "해당 ID의 영화를 찾을 수 없습니다."),

    // 상영관 관련 에러
    SCREEN_NOT_FOUND_ERROR(404, "해당 상영관을 찾을 수 없습니다."),
    ALREADY_SCREEN_SCHEDULE_ERROR(409, "해당 시간에 이미 등록된 상영 스케줄이 있습니다."),

    // 예매 관련 에러
    SEAT_NOT_FOUND_ERROR(404, "좌석 정보를 찾을 수 없습니다."),
    RESERVATION_NOT_FOUND_ERROR(404, "존재하지 않는 예매 내역입니다."),
    ALREADY_RESERVED_SEAT_ERROR(409, "이미 선택된 좌석이 포함되어 있습니다."),
    ALREADY_CANCELED_RESERVATION_ERROR(409, "이미 취소된 예매입니다."),

    // 상영 일정 관련 에러
    SCHEDULE_NOT_FOUND_ERROR(404, "상영 일정을 찾을 수 없습니다."),

    // 매점 관련 에러
    PRODUCT_NOT_FOUND_ERROR(404, "해당 상품을 찾을 수 없습니다."),
    STOCK_NOT_FOUND_ERROR(404, "해당 지점에 상품 재고 정보가 없습니다."),
    OUT_OF_STOCK_ERROR(400, "재고가 부족하여 주문할 수 없습니다."),
    ORDER_NOT_FOUND_ERROR(404, "해당 주문을 찾을 수 없습니다."),
    ORDER_NOT_MINE(400, "본인의 주문 내역만 조회할 수 있습니다."),

    // 결제 관련 에러
    PAYMENT_NOT_FOUND(404, "존재하지 않는 결제 내역입니다."),
    STORE_NOT_FOUND(404, "존재하지 않는 가맹점입니다."),
    STORE_ID_MISMATCH(403, "토큰의 가맹점과 요청의 storeId가 일치하지 않습니다."),
    DUPLICATE_PAYMENT_ID(409, "이미 존재하는 결제 ID입니다."),
    PAYMENT_NOT_CANCELLABLE(409, "PAID 상태가 아닌 결제는 취소할 수 없습니다."),
    ALREADY_CANCELLED_PAYMENT(409, "이미 취소된 결제 내역입니다."),
    PAYMENT_FAILED(500, "외부 결제 서버 에러로 결제에 실패하였습니다."),
    INVALID_PAYMENT_STATUS(400, "잘못된 결제 상태 변경 시도입니다."),
    PAYMENT_ROLLBACK_FAILED(500,"결제 취소 API는 성공했으나, 내부 데이터 복구에 실패했습니다. (수동 확인 필요)"),

    // 서버가 처리 할 방법을 모르는 경우 발생
    INTERNAL_SERVER_ERROR(500, "Internal Server Error Exception"),

    ;

    private final int statusCode;

    private final String message;

    ErrorCode(final int statusCode, final String message) {
        this.statusCode = statusCode;
        this.message = message;
    }

}
