package com.ceos23.spring_boot.domain.movie.dto;

import com.ceos23.spring_boot.domain.movie.entity.Movie;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

public record MovieInfo(
        Long id,
        String title,
        Integer runtime,
        LocalDate releaseDate,
        String ageRating,
        BigDecimal averageRating,
        String posterUrl,
        String description
) implements Serializable {
    public static MovieInfo from(Movie movie) {
        return new MovieInfo(
                movie.getId(),
                movie.getTitle(),
                movie.getRuntime(),
                movie.getReleaseDate(),
                movie.getAgeRating(),
                movie.getAverageRating(),
                movie.getPosterUrl(),
                movie.getDescription()
        );
    }
}
