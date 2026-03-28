package com.ceos.spring_boot.domain.cinema.dto;

import com.ceos.spring_boot.domain.cinema.entity.CinemaStatus;
import io.swagger.v3.oas.annotations.media.Schema;

public record CinemaCreateRequest(

        @Schema(description = "영화관 지점명", example = "CGV 신촌")
        String name,

        @Schema(description = "지역", example = "서울")
        String region,

        @Schema(description = "주소", example = "서울특별시 서대문구 신촌로 129")
        String address,

        @Schema(description = "영화관 상태", example = "OPERATING")
        CinemaStatus status
) {
}
