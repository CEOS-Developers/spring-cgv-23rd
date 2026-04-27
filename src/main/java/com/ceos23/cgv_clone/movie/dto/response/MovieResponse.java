package com.ceos23.cgv_clone.movie.dto.response;

import com.ceos23.cgv_clone.movie.entity.Movie;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class MovieResponse {
    private Long id;
    private String name;
    private int runningTime;
    private int ageRestriction;

    private double reservationRate;
    private int totalViewers;
    private double eggRate;

    public static MovieResponse from(Movie movie) {
        return MovieResponse.builder()
                .id(movie.getId())
                .name(movie.getName())
                .runningTime(movie.getRunningTime())
                .ageRestriction(movie.getAgeRestriction())
                // 영화 상세 내역 없을 경우 0으로 처리.
                .reservationRate(movie.getMovieStatistic() != null ? movie.getMovieStatistic().getReservationRate() : 0)
                .totalViewers(movie.getMovieStatistic() != null ? movie.getMovieStatistic().getTotalViewers() : 0)
                .eggRate(movie.getMovieStatistic() != null ? movie.getMovieStatistic().getEggRate() : 0)
                .build();
    }
}
