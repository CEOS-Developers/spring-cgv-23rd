package com.ceos.spring_cgv_23rd.domain.screening.adapter.in.web.dto.response;

import com.ceos.spring_cgv_23rd.domain.movie.domain.AgeRating;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

public class ScreeningResponse {

    @Builder
    public record ScreeningByMovieResponse(
            Long hallId,
            String hallName,
            String hallTypeName,
            List<ScreeningInfo> screenings
    ) {
    }

    @Builder
    public record ScreeningByTheaterResponse(
            Long movieId,
            String movieTitle,
            String posterUrl,
            AgeRating ageRating,
            List<ScreeningWithHallInfo> screenings
    ) {
    }

    @Builder
    public record ScreeningInfo(
            Long screeningId,
            LocalDateTime startAt,
            LocalDateTime endAt,
            Integer totalSeats,
            Integer remainingSeats
    ) {
    }

    @Builder
    public record ScreeningWithHallInfo(
            Long screeningId,
            String hallName,
            String hallTypeName,
            LocalDateTime startAt,
            LocalDateTime endAt,
            Integer totalSeats,
            Integer remainingSeats
    ) {
    }

}
