package com.ceos.spring_boot.domain.movie.dto;

import com.ceos.spring_boot.domain.movie.entity.AgeRating;
import com.ceos.spring_boot.domain.movie.entity.Movie;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;

public record MovieResponse(
        @Schema(description = "영화 id", example = "1")
        Long id,

        @Schema(description = "영화 제목", example = "왕과 사는 남자")
        String title,

        @Schema(description = "러닝타임", example = "120(분)")
        Integer runningTime,

        @Schema(description = "장르", example = "드라마")
        String genre,

        @Schema(description = "개봉일", example = "2026-03-10")
        LocalDate releaseDate,

        @Schema(description = "연령제한", example = "AGE_12")
        AgeRating ageRating
) {
    public static MovieResponse from(Movie movie) {
        return new MovieResponse(
                movie.getId(),
                movie.getTitle(),
                movie.getRunningTime(),
                movie.getGenre(),
                movie.getReleaseDate(),
                movie.getAgeRating()
        );
    }
}
