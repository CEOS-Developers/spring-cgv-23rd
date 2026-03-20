package com.ceos23.spring_boot.controller.theater.dto;

import com.ceos23.spring_boot.domain.theater.dto.TheaterSearchCommand;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "영화관 조회 요청 DTO")
public record TheaterSearchRequest(
        @Schema(description = "영화관 위치", example = "서울")
        String location
) {
    public TheaterSearchCommand toCommand() {
        return new TheaterSearchCommand(location);
    }
}
