package com.ceos23.spring_cgv_23rd.global.Exception;

import com.ceos23.spring_cgv_23rd.global.DTO.ErrDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.UnsatisfiedServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ErrDTO> handleCustomException(CustomException e) {
        ErrorCode code = e.getCode();
        log.warn("에러 발생. 에러코드: {}", e.getCode().getErrorCode(), e);

        return ResponseEntity
                .status(code.getStatus())
                .body(ErrDTO.create(code));
    }

    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<ErrDTO> handleCustomException(NullPointerException npe) {
        log.error("NPE 발생. message: {}", npe.getMessage(), npe);
        log.error(String.valueOf(npe.fillInStackTrace()));

        ErrorCode code = ErrorCode.INTERNAL_SERVER_ERROR;

        return ResponseEntity
                .status(code.getStatus())
                .body(ErrDTO.create(code));
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrDTO> handleCustomException(IllegalStateException ile){
        log.error("IllegalStateException 발생, message: {}", ile.getMessage(), ile);
        log.error(String.valueOf(ile.fillInStackTrace()));

        ErrorCode code = ErrorCode.INTERNAL_SERVER_ERROR;

        return ResponseEntity
                .status(code.getStatus())
                .body(ErrDTO.create(code));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrDTO> handleCustomException(IllegalArgumentException iae){
        log.error("IllegalArgumentException 발생, message: {}", iae.getMessage(), iae);
        log.error(String.valueOf(iae.fillInStackTrace()));

        ErrorCode code = ErrorCode.INTERNAL_SERVER_ERROR;

        return ResponseEntity
                .status(code.getStatus())
                .body(ErrDTO.create(code));
    }

    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<ErrDTO> handleCustomException(DataAccessException dae){
        log.error("DataAccessException 발생, message: {}", dae.getMessage(), dae);
        log.error(String.valueOf(dae.fillInStackTrace()));

        ErrorCode code = ErrorCode.DATA_ACCESS_EXCEPTION;

        return ResponseEntity
                .status(code.getStatus())
                .body(ErrDTO.create(code));
    }

    @ExceptionHandler(UnsatisfiedServletRequestParameterException.class)
    public ResponseEntity<ErrDTO> handleCustomException(UnsatisfiedServletRequestParameterException usrpe){
        log.error("UnsatisfiedServletRequestParameterException 발생, message: {}", usrpe.getMessage(), usrpe);
        log.error(String.valueOf(usrpe.fillInStackTrace()));

        ErrorCode code = ErrorCode.UN_SATISFIED_PARAMETERS;

        return ResponseEntity
                .status(code.getStatus())
                .body(ErrDTO.create(code));
    }
}
