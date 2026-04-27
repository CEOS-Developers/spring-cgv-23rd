package com.ceos23.cgv.global.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice // 모든 Controller에서 발생하는 예외를 전역적으로 캐치합니다.
public class GlobalExceptionHandler {

    /**
     * [Exception] 비즈니스 로직에서 발생하는 커스텀 예외
     */
    @ExceptionHandler(CustomException.class)
    protected ResponseEntity<ErrorResponse> handleCustomException(CustomException e) {
        log.error("handleCustomException: {}", e.getErrorCode().getMessage());
        return ResponseEntity
                .status(e.getErrorCode().getStatus())
                .body(ErrorResponse.from(e.getErrorCode()));
    }

    /**
     * [Exception] @Valid 검증 실패 시 발생 (예: DTO 조건 위반)
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        log.error("handleMethodArgumentNotValidException", e);
        return ResponseEntity
                .status(ErrorCode.INVALID_INPUT_VALUE.getStatus())
                .body(ErrorResponse.of(ErrorCode.INVALID_INPUT_VALUE, e.getBindingResult()));
    }

    /**
     * [Exception] 지원하지 않는 HTTP 메서드 호출 시 발생 (예: POST인데 GET으로 호출)
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    protected ResponseEntity<ErrorResponse> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {
        log.error("handleHttpRequestMethodNotSupportedException", e);
        return ResponseEntity
                .status(ErrorCode.METHOD_NOT_ALLOWED.getStatus())
                .body(ErrorResponse.from(ErrorCode.METHOD_NOT_ALLOWED));
    }

    /**
     * [Exception] 요청 본문 역직렬화 중 발생한 예외 처리
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    protected ResponseEntity<ErrorResponse> handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        log.error("handleHttpMessageNotReadableException", e);

        CustomException customException = findCustomException(e);
        if (customException != null) {
            return handleCustomException(customException);
        }

        return ResponseEntity
                .status(ErrorCode.INVALID_INPUT_VALUE.getStatus())
                .body(ErrorResponse.from(ErrorCode.INVALID_INPUT_VALUE));
    }

    /**
     * [Exception] 외부 변환/프레임워크에서 발생하는 기본 IllegalArgumentException 처리
     */
    @ExceptionHandler(IllegalArgumentException.class)
    protected ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException e) {
        log.error("handleIllegalArgumentException", e);
        return ResponseEntity
                .status(ErrorCode.INVALID_INPUT_VALUE.getStatus())
                .body(ErrorResponse.from(ErrorCode.INVALID_INPUT_VALUE));
    }

    /**
     * [Exception] 그 외 서버에서 발생하는 모든 예상치 못한 에러 (NullPointerException 등 500 에러)
     */
    @ExceptionHandler(Exception.class)
    protected ResponseEntity<ErrorResponse> handleException(Exception e) {
        log.error("handleException", e);
        return ResponseEntity
                .status(ErrorCode.INTERNAL_SERVER_ERROR.getStatus())
                .body(ErrorResponse.from(ErrorCode.INTERNAL_SERVER_ERROR));
    }

    private CustomException findCustomException(Throwable throwable) {
        Throwable current = throwable;
        while (current != null) {
            if (current instanceof CustomException customException) {
                return customException;
            }
            current = current.getCause();
        }
        return null;
    }
}
