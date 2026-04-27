package com.ceos23.spring_boot.controller.user.dto.FavoriteTheaterDto;

import com.ceos23.spring_boot.domain.user.dto.FavoriteTheaterInfo;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "찜한 영화관 상세 데이터")
public record FavoriteTheaterResponse(
        @Schema(description = "영화관 고유 ID", example = "1")
        Long theaterId,

        @Schema(description = "영화관 지점명", example = "CGV 강남")
        String name,

        @Schema(description = "영화관 위치", example = "서울")
        String location
) {
    public static FavoriteTheaterResponse from(FavoriteTheaterInfo info) {
        return new FavoriteTheaterResponse(
                info.theater().getId(),
                info.theater().getName(),
                info.theater().getLocation()
        );
    }
}
