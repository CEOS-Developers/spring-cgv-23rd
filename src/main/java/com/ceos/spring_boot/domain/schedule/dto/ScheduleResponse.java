package com.ceos.spring_boot.domain.schedule.dto;

import com.ceos.spring_boot.domain.schedule.entity.Schedule;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

public record ScheduleResponse(

        @Schema(description = "스케쥴 id", example = "1")
        Long id,

        @Schema(description = "영화 제목", example = "왕과 사는 남자")
        String movieTitle,

        @Schema(description = "영화관 이름", example = "CGV 강변")
        String cinemaName,

        @Schema(description = "스크린 이름", example = "일반관")
        String screenName,

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
        @Schema(description = "상영 시작 날짜/시간", example = "2026-03-24 10:00:00")
        LocalDateTime startAt,

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
        @Schema(description = "상영 종료 날짜/시간", example = "2026-03-24 11:57:00")
        LocalDateTime endAt
) {
    public static ScheduleResponse from(Schedule schedule) {
        return new ScheduleResponse(
                schedule.getId(),
                schedule.getMovie().getTitle(),
                schedule.getScreen().getCinema().getName(),
                schedule.getScreen().getName(),
                schedule.getStartAt(),
                schedule.getEndAt()
        );
    }
}