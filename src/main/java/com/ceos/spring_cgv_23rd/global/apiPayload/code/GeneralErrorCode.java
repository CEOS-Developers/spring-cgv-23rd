package com.ceos.spring_cgv_23rd.global.apiPayload.code;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum GeneralErrorCode implements BaseErrorCode {

    // 인증 에러
    MISSING_AUTH_INFO(HttpStatus.UNAUTHORIZED, "AUTH_4011", "인증 정보가 누락되었습니다."),
    INVALID_LOGIN(HttpStatus.UNAUTHORIZED, "AUTH_4012", "올바르지 않은 아이디, 혹은 비밀번호입니다."),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "AUTH_4013", "유효하지 않은 토큰입니다."),
    TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "AUTH_4014", "토큰이 만료되었습니다."),
    FORBIDDEN(HttpStatus.FORBIDDEN, "AUTH_4031", "접근 권한이 없습니다."),

    // 요청/파라미터 에러
    MISSING_PARAMETER(HttpStatus.BAD_REQUEST, "REQ_4001", "필수 파라미터가 누락되었습니다."),
    INVALID_PARAMETER(HttpStatus.BAD_REQUEST, "REQ_4002", "파라미터 형식이 잘못되었습니다."),
    INVALID_BODY_TYPE(HttpStatus.BAD_REQUEST, "REQ_4003", "요청 본문의 형식이 잘못되었거나, 허용되지 않은 값이 포함되어 있습니다."),
    UNSUPPORTED_CONTENT_TYPE(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "REQ_4151", "지원하지 않는 Content-Type입니다."),
    DUPLICATE_RESOURCE(HttpStatus.CONFLICT, "REQ_4091", "이미 존재하는 데이터입니다."),

    // API/라우팅 에러
    API_NOT_FOUND(HttpStatus.NOT_FOUND, "API_4041", "존재하지 않는 API입니다."),
    RESOURCE_NOT_FOUND(HttpStatus.NOT_FOUND, "REQ_4042", "요청한 리소스를 찾을 수 없습니다."),
    METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "API_4051", "지원하지 않는 HTTP 메서드입니다."),

    // 서버 내부 에러
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "SERVER_5001", "서버 내부 오류입니다."),
    SERVICE_UNAVAILABLE(HttpStatus.SERVICE_UNAVAILABLE, "SERVER_5031", "서버가 일시적으로 불안정합니다."),
    EXTERNAL_SERVICE_TIMEOUT(HttpStatus.GATEWAY_TIMEOUT, "SERVER_5041", "외부 서비스 응답 지연");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
