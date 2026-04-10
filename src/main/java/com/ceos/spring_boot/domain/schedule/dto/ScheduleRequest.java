package com.ceos.spring_boot.domain.schedule.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

public record ScheduleRequest(

        @Schema(description = "영화 id", example = "1")
        Long movieId,

        @Schema(description = "스크린 id", example = "2")
        Long screenId,

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
        @Schema(description = "상영 시작 시간", example = "2026-03-25 14:30:00")
        LocalDateTime startAt
) {}
