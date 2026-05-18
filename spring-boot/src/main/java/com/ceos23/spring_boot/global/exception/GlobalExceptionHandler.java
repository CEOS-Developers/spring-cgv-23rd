package com.ceos23.spring_boot.global.exception;

import com.ceos23.spring_boot.exception.CustomException;
import com.ceos23.spring_boot.global.response.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.PessimisticLockingFailureException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ErrorResponse> handleCustomException(CustomException e) {
        ErrorCode errorCode = e.getErrorCode();

        log.warn("business_error errorCode={} message={}",
                errorCode.getCode(),
                errorCode.getMessage()
        );

        ErrorResponse response = ErrorResponse.builder()
                .status(errorCode.getStatus())
                .code(errorCode.getCode())
                .message(errorCode.getMessage())
                .build();

        return ResponseEntity
                .status(errorCode.getStatus())
                .body(response);
    }

    @ExceptionHandler(PessimisticLockingFailureException.class)
    public ResponseEntity<ErrorResponse> handlePessimisticLockingFailureException(
            PessimisticLockingFailureException e
    ) {
        log.warn("lock_failed errorCode={} message={}",
                ErrorCode.LOCK_ACQUISITION_FAILED.getCode(),
                ErrorCode.LOCK_ACQUISITION_FAILED.getMessage()
        );

        ErrorResponse response = ErrorResponse.builder()
                .status(ErrorCode.LOCK_ACQUISITION_FAILED.getStatus())
                .code(ErrorCode.LOCK_ACQUISITION_FAILED.getCode())
                .message(ErrorCode.LOCK_ACQUISITION_FAILED.getMessage())
                .build();

        return ResponseEntity
                .status(ErrorCode.LOCK_ACQUISITION_FAILED.getStatus())
                .body(response);
    }

    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<ErrorResponse> handleDataAccessException(DataAccessException e) {
        log.error("database_error errorCode={} message={}",
                ErrorCode.DATABASE_ERROR.getCode(),
                ErrorCode.DATABASE_ERROR.getMessage(),
                e
        );

        ErrorResponse response = ErrorResponse.builder()
                .status(ErrorCode.DATABASE_ERROR.getStatus())
                .code(ErrorCode.DATABASE_ERROR.getCode())
                .message(ErrorCode.DATABASE_ERROR.getMessage())
                .build();

        return ResponseEntity
                .status(ErrorCode.DATABASE_ERROR.getStatus())
                .body(response);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException e) {
        log.warn("bad_request errorCode={} message={}",
                ErrorCode.BAD_REQUEST.getCode(),
                e.getMessage()
        );

        ErrorResponse response = ErrorResponse.builder()
                .status(ErrorCode.BAD_REQUEST.getStatus())
                .code(ErrorCode.BAD_REQUEST.getCode())
                .message(e.getMessage())
                .build();

        return ResponseEntity
                .status(ErrorCode.BAD_REQUEST.getStatus())
                .body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception e) {
        log.error("internal_server_error errorCode={} message={}",
                ErrorCode.INTERNAL_ERROR.getCode(),
                ErrorCode.INTERNAL_ERROR.getMessage(),
                e
        );

        ErrorResponse response = ErrorResponse.builder()
                .status(ErrorCode.INTERNAL_ERROR.getStatus())
                .code(ErrorCode.INTERNAL_ERROR.getCode())
                .message(ErrorCode.INTERNAL_ERROR.getMessage())
                .build();

        return ResponseEntity
                .status(ErrorCode.INTERNAL_ERROR.getStatus())
                .body(response);
    }
}