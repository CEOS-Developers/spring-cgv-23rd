package com.ceos23.spring_cgv_23rd.ETC;

import com.ceos23.spring_cgv_23rd.ETC.DTO.ErrDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpClientErrorException;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(NullPointerException.class)
    protected ResponseEntity<ErrDTO> npeHandler(NullPointerException npe){
        ErrDTO err = ErrDTO.builder()
                .errCode(HttpStatus.BAD_REQUEST.value())
                .errMessage(npe.getMessage())
                .build();

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(err);
    }
}
