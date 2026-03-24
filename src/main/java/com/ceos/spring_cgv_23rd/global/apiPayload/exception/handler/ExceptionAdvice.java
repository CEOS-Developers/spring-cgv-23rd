package com.ceos.spring_cgv_23rd.global.apiPayload.exception.handler;

import com.ceos.spring_cgv_23rd.global.apiPayload.code.BaseErrorCode;
import com.ceos.spring_cgv_23rd.global.apiPayload.code.GeneralErrorCode;
import com.ceos.spring_cgv_23rd.global.apiPayload.exception.GeneralException;
import com.ceos.spring_cgv_23rd.global.apiPayload.response.ApiResponse;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.core.PropertyReferenceException;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

@Slf4j
@RestControllerAdvice
public class ExceptionAdvice {

    /**
     * [커스텀 예외] 직접 정의한 예외(GeneralException)을 처리
     */
    @ExceptionHandler(GeneralException.class)
    public ResponseEntity<ApiResponse<Object>> handleCustomException(GeneralException e) {
        BaseErrorCode code = e.getErrorCode();

        log.warn("CustomException: {}", e.getErrorCode().getMessage());

        return ResponseEntity
                .status(code.getHttpStatus())
                .body(ApiResponse.onFailure(code, e.getMessage()));
    }

    /**
     * [유효성 검사 예외] @Valid를 통한 유효성 검증 실패 처리
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Object>> handleValidationException(MethodArgumentNotValidException e) {
        var errors = e.getBindingResult().getFieldErrors()
                .stream()
                .map(fld -> String.format("[%s] %s", fld.getField(), fld.getDefaultMessage()))
                .toList();

        log.warn("MethodArgumentNotValidException: {}", errors);

        BaseErrorCode code = GeneralErrorCode.INVALID_PARAMETER;

        return ResponseEntity
                .status(code.getHttpStatus())
                .body(ApiResponse.onFailure(code, errors));
    }

    /**
     * [파라미터 타입 불일치 예외] 쿼리 파라미터나 PathVariable의 타입 불일치 처리
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiResponse<Object>> handleTypeMismatchException(MethodArgumentTypeMismatchException e) {
        String message = String.format("%s 필드의 타입이 잘못되었습니다.", e.getName());

        log.warn("TypeMismatchException: {}", e.getMessage());

        BaseErrorCode code = GeneralErrorCode.INVALID_PARAMETER;

        return ResponseEntity
                .status(code.getHttpStatus())
                .body(ApiResponse.onFailure(code, message));
    }

    /**
     * [메세지 읽기 실패 예외] json 바디 형식의 불일치 처리
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<Object>> handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        BaseErrorCode code = GeneralErrorCode.INVALID_BODY_TYPE;

        log.warn("HttpMessageNotReadableException: {}", e.getMessage());

        return ResponseEntity
                .status(code.getHttpStatus())
                .body(ApiResponse.onFailure(code, code.getMessage()));
    }

    /**
     * [JPA 필드 참조 예외] JPA에서 sort나 검색시 존재하지 않는 필드명 처리
     */
    @ExceptionHandler(PropertyReferenceException.class)
    public ResponseEntity<ApiResponse<Object>> handlePropertyReferenceException(PropertyReferenceException e) {
        BaseErrorCode code = GeneralErrorCode.INVALID_PARAMETER;

        log.warn("PropertyReferenceException: {}", e.getMessage());

        return ResponseEntity
                .status(code.getHttpStatus())
                .body(ApiResponse.onFailure(code, code.getMessage()));
    }

    /**
     * [데이터 무결성 위반 예외] DB 유니크 제약 조건이나 외래키 제약 위반 처리
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiResponse<Object>> handleDataIntegrityViolationException(DataIntegrityViolationException e) {
        BaseErrorCode code = GeneralErrorCode.DUPLICATE_RESOURCE;

        log.warn("DataIntegrityViolationException: {}", e.getMessage());

        return ResponseEntity
                .status(code.getHttpStatus())
                .body(ApiResponse.onFailure(code, code.getMessage()));
    }

    /**
     * [Bean Validation 예외] @NotNull, @Min 등 Bean Validation 제약 조건 위반 처리
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponse<Object>> handleConstraintViolationException(ConstraintViolationException e) {
        var errors = e.getConstraintViolations().stream()
                .map(v -> String.format("[%s] %s", v.getPropertyPath().toString(), v.getMessage()))
                .toList();

        BaseErrorCode code = GeneralErrorCode.INVALID_PARAMETER;

        log.warn("ConstraintViolationException: {}", e.getMessage());

        return ResponseEntity
                .status(code.getHttpStatus())
                .body(ApiResponse.onFailure(code, errors));
    }

    /**
     * [API 경로 없음 예외] 존재하지 않는 API 경로 요청 처리
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleNoHandlerFoundException(NoHandlerFoundException e) {
        BaseErrorCode code = GeneralErrorCode.API_NOT_FOUND;

        log.warn("NoHandlerFoundException: {}", e.getMessage());

        return ResponseEntity
                .status(code.getHttpStatus())
                .body(ApiResponse.onFailure(code, code.getMessage()));
    }

    /**
     * [HTTP 메서드 불일치 예외] 지원하지 않는 HTTP 메서드 요청 처리
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiResponse<Object>> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {
        BaseErrorCode code = GeneralErrorCode.METHOD_NOT_ALLOWED;

        log.warn("HttpRequestMethodNotSupportedException: {}", e.getMessage());

        return ResponseEntity
                .status(code.getHttpStatus())
                .body(ApiResponse.onFailure(code, code.getMessage()));
    }

    /**
     * [최상위 예외] 위의 예외를 제외한 모든 예외를 처리
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleException(Exception e) {
        BaseErrorCode code = GeneralErrorCode.INTERNAL_SERVER_ERROR;

        log.warn("Exception: {}", e.getMessage());

        return ResponseEntity
                .status(code.getHttpStatus())
                .body(ApiResponse.onFailure(code, code.getMessage()));
    }
}
