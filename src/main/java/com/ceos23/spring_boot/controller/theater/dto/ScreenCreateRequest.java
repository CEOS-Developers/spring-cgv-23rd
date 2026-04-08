package com.ceos23.spring_boot.controller.theater.dto;

import com.ceos23.spring_boot.domain.theater.dto.ScreenCreateCommand;
import com.ceos23.spring_boot.domain.theater.dto.TheaterCreateCommand;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(description = "상영관 생성 요청 DTO")
public record ScreenCreateRequest(

        @Schema(description = "극장 ID", example = "1")
        @NotNull(message = "극장 ID를 입력해주세요.")
        Long theaterId,

        @Schema(description = "상영관 종류 ID", example = "1")
        @NotNull(message = "상영관 종류 ID를 입력해주세요.")
        Long screenTypeId,

        @Schema(description = "상영관명", example = "1관")
        @NotBlank(message = "상영관명을 입력해주세요.")
        String screenName

) {
    public ScreenCreateCommand toCommand() {
        return new ScreenCreateCommand(
                this.theaterId,
                this.screenTypeId,
                this.screenName
        );
    }
}