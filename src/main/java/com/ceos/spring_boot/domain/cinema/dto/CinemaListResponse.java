package com.ceos.spring_boot.domain.cinema.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public record CinemaListResponse(
        List<CinemaResponse> cinemas,

        @Schema(description = "영화관 총 지점 수", example = "50")
        int count
) {
    public static CinemaListResponse from(List<CinemaResponse> cinemas) {
        return new CinemaListResponse(cinemas, cinemas.size());
    }
}
