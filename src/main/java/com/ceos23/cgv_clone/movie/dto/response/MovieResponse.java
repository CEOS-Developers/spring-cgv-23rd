package com.ceos23.cgv_clone.movie.dto.response;

import com.ceos23.cgv_clone.movie.domain.Movie;
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

    private double reservationRate;
    private int totalViewers;
    private double eggRate;

    public static MovieResponse from(Movie movie) {
        return MovieResponse.builder()
                .id(movie.getId())
                .name(movie.getName())
                .runningTime(movie.getRunningTime())
                .reservationRate(movie.getMovieStatistic().getReservationRate())
                .totalViewers(movie.getMovieStatistic().getTotalViewers())
                .eggRate(movie.getMovieStatistic().getEggRate())
                .build();
    }
}
