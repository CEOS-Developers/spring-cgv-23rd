package com.ceos23.spring_boot.controller.theater.dto;

import com.ceos23.spring_boot.domain.theater.dto.TheaterCreateCommand;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "영화관 생성 요청 DTO")
public record TheaterCreateRequest(
        @Schema(description = "영화관 지점명", example = "CGV 강남점")
        @NotBlank(message = "영화관 지점명을 입력해주세요.")
        String name,

        @Schema(description = "영화관 위치", example = "서울")
        @NotBlank(message = "지역을 입력해주세요.")
        String location
) {
    public TheaterCreateCommand toCommand() {
        return new TheaterCreateCommand(
                this.name,
                this.location
        );
    }
}
