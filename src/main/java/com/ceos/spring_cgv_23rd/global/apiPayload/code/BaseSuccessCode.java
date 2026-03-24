package com.ceos.spring_cgv_23rd.global.apiPayload.code;

import org.springframework.http.HttpStatus;

public interface BaseSuccessCode {
    HttpStatus getHttpStatus();

    String getCode();

    String getMessage();
}
