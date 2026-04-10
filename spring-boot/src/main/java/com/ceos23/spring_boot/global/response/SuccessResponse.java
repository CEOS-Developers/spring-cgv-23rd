package com.ceos23.spring_boot.global.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SuccessResponse<T> {
    private int status;
    private String code;
    private T data;
}