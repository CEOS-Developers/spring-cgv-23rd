package com.ceos23.spring_cgv_23rd.global.DTO;

import com.ceos23.spring_cgv_23rd.global.Exception.ErrorCode;

public record ErrDTO(
        String errCode, String errMessage
){
    public static ErrDTO create(ErrorCode e){
        return new ErrDTO(
                e.getErrorCode(), e.getErrorMessage()
        );
    }
}
