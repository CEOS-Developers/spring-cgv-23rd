package com.ceos.spring_cgv_23rd.domain.movie.adapter.in.web.mapper;

import com.ceos.spring_cgv_23rd.domain.movie.adapter.in.web.dto.response.MovieResponse;
import com.ceos.spring_cgv_23rd.domain.movie.application.dto.result.ToggleMovieLikeResult;
import com.ceos.spring_cgv_23rd.domain.movie.domain.Movie;
import com.ceos.spring_cgv_23rd.domain.movie.domain.MovieCredit;
import com.ceos.spring_cgv_23rd.domain.movie.domain.MovieMedia;
import com.ceos.spring_cgv_23rd.domain.movie.domain.MovieStatistic;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MovieResponseMapper {

    public List<MovieResponse.MovieListResponse> toMovieListResponse(List<Movie> movies) {
        return movies.stream()
                .map(this::toMovieListDto)
                .toList();
    }

    public MovieResponse.MovieDetailResponse toMovieDetailResponse(Movie movie) {
        return MovieResponse.MovieDetailResponse.builder()
                .id(movie.getId())
                .title(movie.getTitle())
                .prolog(movie.getProlog())
                .status(movie.getStatus())
                .duration(movie.getDuration())
                .genre(movie.getGenre())
                .ageRating(movie.getAgeRating())
                .releasedAt(movie.getReleasedAt())
                .posterUrl(movie.getPosterUrl())
                .statistic(toStatisticDto(movie.getMovieStatistic()))
                .build();
    }

    public List<MovieResponse.MovieCreditResponse> toMovieCreditResponse(List<MovieCredit> credits) {
        return credits.stream()
                .map(credit -> MovieResponse.MovieCreditResponse.builder()
                        .contributorId(credit.getContributor().getId())
                        .name(credit.getContributor().getName())
                        .profileImageUrl(credit.getContributor().getProfileImageUrl())
                        .roleType(credit.getRoleType())
                        .build())
                .toList();
    }

    public List<MovieResponse.MovieMediaResponse> toMovieMediaResponse(List<MovieMedia> medias) {
        return medias.stream()
                .map(media -> MovieResponse.MovieMediaResponse.builder()
                        .id(media.getId())
                        .mediaType(media.getMediaType())
                        .mediaUrl(media.getMediaUrl())
                        .build())
                .toList();
    }

    public MovieResponse.MovieLikeResponse toMovieLikeResponse(ToggleMovieLikeResult result) {
        return MovieResponse.MovieLikeResponse.builder()
                .movieId(result.movieId())
                .liked(result.liked())
                .build();
    }

    private MovieResponse.MovieListResponse toMovieListDto(Movie movie) {
        MovieStatistic stat = movie.getMovieStatistic();
        return MovieResponse.MovieListResponse.builder()
                .id(movie.getId())
                .title(movie.getTitle())
                .posterUrl(movie.getPosterUrl())
                .status(movie.getStatus())
                .ageRating(movie.getAgeRating())
                .reservationRate(stat.getReservationRate())
                .reservationRank(stat.getReservationRank())
                .viewCount(stat.getViewCount())
                .eggCount(stat.getEggCount())
                .releasedAt(movie.getReleasedAt())
                .build();
    }

    private MovieResponse.MovieStatisticResponse toStatisticDto(MovieStatistic stat) {
        return MovieResponse.MovieStatisticResponse.builder()
                .reservationRate(stat.getReservationRate())
                .reservationRank(stat.getReservationRank())
                .viewCount(stat.getViewCount())
                .eggCount(stat.getEggCount())
                .maleReservationRate(stat.getMaleReservationRate())
                .femaleReservationRate(stat.getFemaleReservationRate())
                .ageRates(toAgeRateDto(stat))
                .build();
    }

    private MovieResponse.AgeRateResponse toAgeRateDto(MovieStatistic stat) {
        return MovieResponse.AgeRateResponse.builder()
                .age10sRate(stat.getAge10sRate())
                .age20sRate(stat.getAge20sRate())
                .age30sRate(stat.getAge30sRate())
                .age40sRate(stat.getAge40sRate())
                .age50sRate(stat.getAge50sRate())
                .build();
    }
}
