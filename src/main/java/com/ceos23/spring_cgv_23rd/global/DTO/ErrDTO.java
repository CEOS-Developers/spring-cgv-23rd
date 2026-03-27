package com.ceos23.spring_cgv_23rd.global.DTO;

import lombok.Builder;

@Builder
public record ErrDTO(
        int errCode, String errMessage
){
}
