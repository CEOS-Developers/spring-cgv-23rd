package com.ceos23.spring_boot.controller.theater.dto;

import com.ceos23.spring_boot.domain.theater.dto.TheaterInfo;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "영화관 상세 응답 데이터")
public record TheaterResponse(

        @Schema(description = "영화관 고유 ID", example = "1")
        Long id,

        @Schema(description = "영화관 지점명", example = "CGV 강남")
        String name,

        @Schema(description = "영화관 위치", example = "서울")
        String location
) {

    public static TheaterResponse from(TheaterInfo theaterInfo) {
        return new TheaterResponse(
                theaterInfo.id(),
                theaterInfo.name(),
                theaterInfo.location()
        );
    }
}
