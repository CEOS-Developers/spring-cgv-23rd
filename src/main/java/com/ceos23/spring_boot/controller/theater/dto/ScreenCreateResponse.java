package com.ceos23.spring_boot.controller.theater.dto;

import com.ceos23.spring_boot.domain.theater.dto.ScreenInfo;
import com.ceos23.spring_boot.domain.theater.dto.TheaterInfo;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "상영관 상세 응답 데이터")
public record ScreenCreateResponse(

        @Schema(description = "상영관 고유 ID", example = "1")
        Long id,

        @Schema(description = "극장 ID", example = "1")
        Long theaterId,

        @Schema(description = "상영관 종류 ID", example = "1")
        Long screenTypeId,

        @Schema(description = "상영관명", example = "1관")
        String screenName
) {

    public static ScreenCreateResponse from(ScreenInfo info) {
        return new ScreenCreateResponse(
                info.id(),
                info.theaterId(),
                info.screenTypeId(),
                info.screenName()
        );
    }
}
