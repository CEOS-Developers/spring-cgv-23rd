package com.ceos.spring_cgv_23rd.domain.movie.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
@AllArgsConstructor
public class Movie {

    private final Long id;
    private final String title;
    private final String prolog;
    private final MovieStatus status;
    private final Integer duration;
    private final Genre genre;
    private final AgeRating ageRating;
    private final LocalDate releasedAt;
    private final String posterUrl;
    private MovieStatistic movieStatistic;

}
