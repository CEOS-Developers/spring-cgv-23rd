package com.ceos23.spring_boot.controller.user.dto.FavoriteMovieDto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "영화 찜 토글 결과 응답")
public record FavoriteMovieToggleResponse(
        @Schema(description = "찜 상태 (true: 찜, false: 찜 안 됨)", example = "true")
        boolean isFavorited
) {
    public static FavoriteMovieToggleResponse from(Boolean isFavorited) {
        return new FavoriteMovieToggleResponse(isFavorited);
    }
}