package com.ceos.spring_cgv_23rd.domain.theater.adapter.in.web.dto.response;

import lombok.Builder;

public class TheaterResponse {

    @Builder
    public record TheaterListResponse(
            Long id,
            String name,
            String address,
            boolean isOpened
    ) {
    }

    @Builder
    public record TheaterDetailResponse(
            Long id,
            String name,
            String address,
            String description,
            boolean isOpened
    ) {
    }

    @Builder
    public record TheaterLikeResponse(
            Long theaterId,
            boolean liked
    ) {
    }
}
