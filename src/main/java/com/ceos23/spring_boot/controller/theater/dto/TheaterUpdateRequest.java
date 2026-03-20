package com.ceos23.spring_boot.controller.theater.dto;

import com.ceos23.spring_boot.domain.theater.dto.TheaterUpdateCommand;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "영화관 수정 요청 DTO")
public record TheaterUpdateRequest(
        @Schema(description = "영화관 지점명", example = "CGV 강남점")
        @NotBlank(message = "수정할 영화관 지점명을 입력해주세요.")
        String name,

        @Schema(description = "영화관 위치", example = "서울")
        @NotBlank(message = "수정할 지역을 입력해주세요.")
        String location
) {
    public TheaterUpdateCommand toCommand() {
        return new TheaterUpdateCommand(
                this.name,
                this.location
        );
    }
}
