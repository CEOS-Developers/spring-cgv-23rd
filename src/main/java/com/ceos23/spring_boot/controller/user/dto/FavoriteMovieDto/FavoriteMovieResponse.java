package com.ceos23.spring_boot.controller.user.dto.FavoriteMovieDto;

import com.ceos23.spring_boot.domain.user.dto.FavoriteMovieInfo;
import com.ceos23.spring_boot.domain.user.dto.FavoriteTheaterInfo;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "찜한 영화 상세 데이터")
public record FavoriteMovieResponse(
        @Schema(description = "영화 고유 ID", example = "1")
        Long theaterId,

        @Schema(description = "영화 제목", example = "CGV 강남")
        String title
) {
    public static FavoriteMovieResponse from(FavoriteMovieInfo info) {
        return new FavoriteMovieResponse(
                info.movie().getId(),
                info.movie().getTitle()
        );
    }
}
