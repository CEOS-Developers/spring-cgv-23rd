package com.ceos23.spring_cgv_23rd.ETC.DTO;

import lombok.Builder;

@Builder
public record ErrDTO(
        int errCode, String errMessage
){
}
