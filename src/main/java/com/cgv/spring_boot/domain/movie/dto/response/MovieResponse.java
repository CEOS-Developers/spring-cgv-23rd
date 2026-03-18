package com.cgv.spring_boot.domain.movie.dto.response;

import com.cgv.spring_boot.domain.movie.entity.Movie;

import java.time.LocalDate;

public record MovieResponse(
        Long id,
        String title,
        int runningTime,
        String genre,
        LocalDate releaseDate
) {
    public static MovieResponse from(Movie movie) {
        return new MovieResponse(
                movie.getId(),
                movie.getTitle(),
                movie.getRunningTime(),
                movie.getGenre(),
                movie.getReleaseDate()
        );
    }
}
