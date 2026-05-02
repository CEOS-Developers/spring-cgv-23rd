package com.ceos23.spring_boot.cgv.dto.screening;

import com.ceos23.spring_boot.cgv.domain.movie.Screening;
import java.time.LocalDateTime;

public record ScreeningResponse(
        Long screeningId,
        Long movieId,
        String movieTitle,
        Long cinemaId,
        String cinemaName,
        Long screenId,
        String screenName,
        String screenType,
        LocalDateTime startTime,
        LocalDateTime endTime
) {
    public static ScreeningResponse from(Screening screening) {
        return new ScreeningResponse(
                screening.getId(),
                screening.getMovie().getId(),
                screening.getMovie().getTitle(),
                screening.getScreen().getCinema().getId(),
                screening.getScreen().getCinema().getName(),
                screening.getScreen().getId(),
                screening.getScreen().getName(),
                screening.getScreen().getScreenType().name(),
                screening.getStartTime(),
                screening.getEndTime()
        );
    }
}
