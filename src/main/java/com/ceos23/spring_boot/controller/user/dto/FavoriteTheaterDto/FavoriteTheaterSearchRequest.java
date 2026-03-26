package com.ceos23.spring_boot.controller.user.dto.FavoriteTheaterDto;

import com.ceos23.spring_boot.domain.user.dto.FavoriteTheaterSearchCommand;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "찜한 영화관 조회 요청 DTO")
public record FavoriteTheaterSearchRequest (
        @NotNull(message = "회원 ID는 필수입니다.")
        @Schema(description = "조회 회원 ID", example = "1")
        Long userId
){
    public FavoriteTheaterSearchCommand toCommand() {
        return new FavoriteTheaterSearchCommand(this.userId);
    }
}
