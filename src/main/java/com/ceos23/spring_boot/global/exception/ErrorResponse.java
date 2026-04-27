package com.ceos23.spring_boot.global.exception;

public record ErrorResponse(
        int status,
        String code,
        String message
){
}
