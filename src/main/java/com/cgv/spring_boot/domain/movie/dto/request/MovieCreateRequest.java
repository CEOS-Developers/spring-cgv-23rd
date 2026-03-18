package com.cgv.spring_boot.domain.movie.dto.request;

import com.cgv.spring_boot.domain.movie.entity.Movie;

import java.time.LocalDate;

public record MovieCreateRequest(
        String title,
        int runningTime,
        String rating,
        LocalDate releaseDate,
        String genre,
        String prologue,
        String posterUrl
) {
    // DTO를 엔티티로 변환
    public Movie toEntity() {
        return Movie.builder()
                .title(title)
                .runningTime(runningTime)
                .rating(rating)
                .releaseDate(releaseDate)
                .genre(genre)
                .prologue(prologue)
                .posterUrl(posterUrl)
                .build();
    }
}
