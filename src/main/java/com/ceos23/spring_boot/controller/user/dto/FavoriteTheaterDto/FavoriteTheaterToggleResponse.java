package com.ceos23.spring_boot.controller.user.dto.FavoriteTheaterDto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "영화관 찜 토글 결과 응답")
public record FavoriteTheaterToggleResponse(
        @Schema(description = "찜 상태 (true: 찜, false: 찜 안 됨)", example = "true")
        boolean isFavorited
) {
    public static FavoriteTheaterToggleResponse from(Boolean isFavorited) {
        return new FavoriteTheaterToggleResponse(isFavorited);
    }
}
