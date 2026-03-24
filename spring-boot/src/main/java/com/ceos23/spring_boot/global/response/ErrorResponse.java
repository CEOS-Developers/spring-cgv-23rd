package com.ceos23.spring_boot.global.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ErrorResponse {

    private int status;
    private String code;
    private String message;

}