package com.ceos23.spring_boot.domain.movie.dto;

import com.ceos23.spring_boot.domain.movie.entity.Movie;

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
) {
    // 🌟 Entity를 Info로 변환하는 팩토리 메서드 (Service에서 사용)
    public static MovieInfo from(Movie movie) { // 파라미터로 Movie 엔티티를 받습니다.
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
