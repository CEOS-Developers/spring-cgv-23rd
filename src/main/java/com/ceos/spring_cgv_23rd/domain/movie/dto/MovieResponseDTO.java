package com.ceos.spring_cgv_23rd.domain.movie.dto;

import com.ceos.spring_cgv_23rd.domain.movie.entity.Movie;
import com.ceos.spring_cgv_23rd.domain.movie.entity.MovieCredit;
import com.ceos.spring_cgv_23rd.domain.movie.entity.MovieMedia;
import com.ceos.spring_cgv_23rd.domain.movie.entity.MovieStatistic;
import com.ceos.spring_cgv_23rd.domain.movie.enums.*;
import lombok.Builder;

import java.time.LocalDate;

public class MovieResponseDTO {

    @Builder
    public record MovieListResponseDTO(
            Long id,
            String title,
            String posterUrl,
            MovieStatus status,
            AgeRating ageRating,
            Double reservationRate,
            Integer reservationRank,
            Long viewCount,
            Double eggCount,
            LocalDate releasedAt
    ) {
        public static MovieListResponseDTO of(Movie movie, MovieStatistic movieStatistic) {
            return MovieListResponseDTO.builder()
                    .id(movie.getId())
                    .title(movie.getTitle())
                    .posterUrl(movie.getPosterUrl())
                    .status(movie.getStatus())
                    .ageRating(movie.getAgeRating())
                    .reservationRate(movieStatistic.getReservationRate())
                    .reservationRank(movieStatistic.getReservationRank())
                    .viewCount(movieStatistic.getViewCount())
                    .eggCount(movieStatistic.getEggCount())
                    .releasedAt(movie.getReleasedAt())
                    .build();
        }
    }

    @Builder
    public record MovieDetailResponseDTO(
            Long id,
            String title,
            String prolog,
            MovieStatus status,
            Integer duration,
            Genre genre,
            AgeRating ageRating,
            LocalDate releasedAt,
            String posterUrl,
            MovieStatisticDTO statistic
    ) {
        public static MovieDetailResponseDTO of(Movie movie, MovieStatistic statistic) {
            return MovieDetailResponseDTO.builder()
                    .id(movie.getId())
                    .title(movie.getTitle())
                    .prolog(movie.getProlog())
                    .status(movie.getStatus())
                    .duration(movie.getDuration())
                    .genre(movie.getGenre())
                    .ageRating(movie.getAgeRating())
                    .releasedAt(movie.getReleasedAt())
                    .posterUrl(movie.getPosterUrl())
                    .statistic(MovieStatisticDTO.from(statistic))
                    .build();
        }
    }

    @Builder
    public record MovieCreditResponseDTO(
            Long contributorId,
            String name,
            String profileImageUrl,
            RoleType roleType
    ) {
        public static MovieCreditResponseDTO from(MovieCredit credit) {
            return MovieCreditResponseDTO.builder()
                    .contributorId(credit.getContributor().getId())
                    .name(credit.getContributor().getName())
                    .profileImageUrl(credit.getContributor().getProfileImageUrl())
                    .roleType(credit.getRoleType())
                    .build();
        }
    }

    @Builder
    public record MovieMediaResponseDTO(
            Long id,
            MediaType mediaType,
            String mediaUrl
    ) {
        public static MovieMediaResponseDTO from(MovieMedia media) {
            return MovieMediaResponseDTO.builder()
                    .id(media.getId())
                    .mediaType(media.getMediaType())
                    .mediaUrl(media.getMediaUrl())
                    .build();
        }
    }

    @Builder
    private record MovieStatisticDTO(
            Double reservationRate,
            Integer reservationRank,
            Long viewCount,
            Double eggCount,
            Double maleReservationRate,
            Double femaleReservationRate,
            AgeRateDTO ageRates
    ) {
        private static MovieStatisticDTO from(MovieStatistic statistic) {

            return MovieStatisticDTO.builder()
                    .reservationRate(statistic.getReservationRate())
                    .reservationRank(statistic.getReservationRank())
                    .viewCount(statistic.getViewCount())
                    .eggCount(statistic.getEggCount())
                    .maleReservationRate(statistic.getMaleReservationRate())
                    .femaleReservationRate(statistic.getFemaleReservationRate())
                    .ageRates(AgeRateDTO.from(statistic))
                    .build();
        }
    }

    @Builder
    private record AgeRateDTO(
            Double age10sRate,
            Double age20sRate,
            Double age30sRate,
            Double age40sRate,
            Double age50sRate
    ) {
        private static AgeRateDTO from(MovieStatistic statistic) {

            return AgeRateDTO.builder()
                    .age10sRate(statistic.getAge10sRate())
                    .age20sRate(statistic.getAge20sRate())
                    .age30sRate(statistic.getAge30sRate())
                    .age40sRate(statistic.getAge40sRate())
                    .age50sRate(statistic.getAge50sRate())
                    .build();
        }
    }
}
