package com.ceos.spring_cgv_23rd.domain.screening.application.dto.result;

import com.ceos.spring_cgv_23rd.domain.movie.domain.AgeRating;

import java.time.LocalDateTime;

public record ScreeningDetailResult(
        Long screeningId,
        Long movieId,
        String movieTitle,
        String posterUrl,
        AgeRating ageRating,
        Long hallId,
        String hallName,
        String hallTypeName,
        LocalDateTime startAt,
        LocalDateTime endAt,
        Integer totalSeats,
        Integer remainingSeats
) {

}
