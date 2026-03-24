package com.ceos.spring_boot.domain.cinema.dto;

import com.ceos.spring_boot.domain.cinema.entity.Cinema;
import com.ceos.spring_boot.domain.cinema.entity.CinemaStatus;
import io.swagger.v3.oas.annotations.media.Schema;

public record CinemaResponse(
        @Schema(description = "영화관 id", example = "1")
        Long id,

        @Schema(description = "영화관 제목", example = "CGV 강변")
        String name,

        @Schema(description = "영화관 지역", example = "서울")
        String region,

        @Schema(description = "영화관 주소", example = "서울특별시 광진구 광나루로56길 85 10층 (구의동, 테크노마트)")
        String address,

        @Schema(description = "영화관 상태", example = "OPERATING")
        CinemaStatus status,

        @Schema(description = "상태 설명", example = "운영중")
        String statusDescription
) {
    public static CinemaResponse from(Cinema cinema) {
        return new CinemaResponse(
                cinema.getId(),
                cinema.getName(),
                cinema.getRegion(),
                cinema.getAddress(),
                cinema.getStatus(),
                cinema.getStatus() != null ? cinema.getStatus().getDescription() : null
        );
    }
}