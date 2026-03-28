package com.ceos.spring_boot.domain.like.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "영화 찜 응답 DTO")
public record CinemaLikeResponse(
        @Schema(description = "사용자 ID", example = "1")
        Long userId,

        @Schema(description = "영화관 ID", example = "5")
        Long cinemaId,

        @Schema(description = "찜 여부 (true: 찜 완료, false: 찜 취소)", example = "true")
        boolean isLiked
) {
    public static CinemaLikeResponse of(Long userId, Long cinemaId, boolean isLiked) {
        return new CinemaLikeResponse(userId, cinemaId, isLiked);
    }
}