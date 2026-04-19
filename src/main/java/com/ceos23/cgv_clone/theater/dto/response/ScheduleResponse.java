package com.ceos23.cgv_clone.theater.dto.response;

import com.ceos23.cgv_clone.theater.entity.Schedule;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class ScheduleResponse {
    private Long id;
    private LocalDateTime startAt;
    private LocalDateTime endAt;

    private String theaterName;
    private String screenName;
    private String movieName;

    public static ScheduleResponse from(Schedule schedule) {

        return ScheduleResponse.builder()
                .id(schedule.getId())
                .startAt(schedule.getStartAt())
                .endAt(schedule.getEndAt())
                .theaterName(schedule.getScreen().getTheater().getName())
                .screenName(schedule.getScreen().getName())
                .movieName(schedule.getMovie().getName())
                .build();
    }
}
