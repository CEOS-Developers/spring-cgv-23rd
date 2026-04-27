package com.ceos23.spring_cgv_23rd.Screen.DTO;

import com.ceos23.spring_cgv_23rd.Screen.Domain.CinemaType;

import java.time.LocalDateTime;

public record ScreeningSearchQueryResultDTO(
        CinemaType type,
        long screeningId,
        String screenName,
        LocalDateTime startTime,
        int runningTime,
        int totalSeatAmount
) {
}

/*
public record ScreeningWrapperDTO(
        long id,
        String screenName,
        LocalTime when,
        LocalTime end,
        Boolean morning,
        int leavingSeatAmount,
        int totalSeatAmount
) {
    public static ScreeningWrapperDTO create(Screen screen, Screening screening, Movie movie, int leavingSeatAmount, int totalSeatAmount){
        return new ScreeningWrapperDTO(
                screening.getId(),
                screen.getScreenName(),
                screening.getStartTimeInLocalTime(),
                screening.calculateEndTime(screening.getStartTimeInLocalTime(), movie.getRunningTime()),
                screening.isMorning(),
                leavingSeatAmount,
                totalSeatAmount
        );
    }
}

 */