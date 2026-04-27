package com.ceos23.spring_cgv_23rd.Screen.DTO.Response;

import com.ceos23.spring_cgv_23rd.Movie.Domain.Movie;
import com.ceos23.spring_cgv_23rd.Screen.DTO.ScreeningSearchQueryResultDTO;
import com.ceos23.spring_cgv_23rd.Screen.Domain.Screen;
import com.ceos23.spring_cgv_23rd.Screen.Domain.Screening;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public record ScreeningWrapperDTO(
        long id,
        String screenName,
        LocalTime when,
        LocalTime end,
        Boolean morning,
        Boolean evening,
        int leavingSeatAmount,
        int totalSeatAmount
) {
    public static ScreeningWrapperDTO create(ScreeningSearchQueryResultDTO req, int reservedSeatQuantity){
        return new ScreeningWrapperDTO(
                req.screeningId(),
                req.screenName(),
                req.startTime().toLocalTime(),
                req.startTime().plusMinutes(req.runningTime()).toLocalTime(),
                req.startTime().toLocalTime().isBefore(LocalTime.of(11,0)),
                req.startTime().toLocalTime().isAfter(LocalTime.of(22,0)),
                req.totalSeatAmount() - reservedSeatQuantity,
                req.totalSeatAmount()
        );
    }

    public static ScreeningWrapperDTO create(Screen screen, Screening screening, Movie movie, int leavingSeatAmount, int totalSeatAmount){
        return new ScreeningWrapperDTO(
                screening.getId(),
                screen.getScreenName(),
                screening.getStartTimeInLocalTime(),
                screening.calculateEndTime(screening.getStartTimeInLocalTime(), movie.getRunningTime()),
                screening.isMorning(),
                screening.isEvening(),
                leavingSeatAmount,
                totalSeatAmount
        );
    }
}
