package com.ceos.spring_cgv_23rd.domain.screening.application.dto.result;

import com.ceos.spring_cgv_23rd.domain.movie.domain.AgeRating;

public record MovieInfoResult(
        Long movieId,
        String movieTitle,
        String posterUrl,
        AgeRating ageRating
) {
}
