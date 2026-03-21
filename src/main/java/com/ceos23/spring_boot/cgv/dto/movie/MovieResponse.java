package com.ceos23.spring_boot.cgv.dto.movie;

import com.ceos23.spring_boot.cgv.domain.movie.Movie;

public record MovieResponse(
        Long movieId,
        String title,
        Integer runningTime,
        String rating,
        String description
) {
    public static MovieResponse from(Movie movie) {
        return new MovieResponse(
                movie.getId(),
                movie.getTitle(),
                movie.getRunningTime(),
                movie.getRating(),
                movie.getDescription()
        );
    }
}