package com.ceos23.spring_cgv_23rd.global.Exception;

import com.ceos23.spring_cgv_23rd.global.DTO.ErrDTO;
import org.springframework.dao.DataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLException;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ErrDTO> handleCustomException(CustomException e) {
        ErrorCode code = e.getCode();

        return ResponseEntity
                .status(code.getStatus())
                .body(ErrDTO.create(code));
    }

    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<ErrDTO> handleCustomException(NullPointerException npe) {
        ErrorCode code = ErrorCode.INTERNAL_SERVER_ERROR;

        return ResponseEntity
                .status(code.getStatus())
                .body(ErrDTO.create(code));
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrDTO> handleCustomException(IllegalStateException ile){
        ErrorCode code = ErrorCode.INTERNAL_SERVER_ERROR;

        return ResponseEntity
                .status(code.getStatus())
                .body(ErrDTO.create(code));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrDTO> handleCustomException(IllegalArgumentException iae){
        ErrorCode code = ErrorCode.INTERNAL_SERVER_ERROR;

        return ResponseEntity
                .status(code.getStatus())
                .body(ErrDTO.create(code));
    }

    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<ErrDTO> handleCustomException(DataAccessException dae){
        ErrorCode code = ErrorCode.DATA_ACCESS_EXCEPTION;

        return ResponseEntity
                .status(code.getStatus())
                .body(ErrDTO.create(code));
    }
}
