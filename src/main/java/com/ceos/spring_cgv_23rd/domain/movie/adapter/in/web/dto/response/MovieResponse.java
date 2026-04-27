package com.ceos.spring_cgv_23rd.domain.movie.adapter.in.web.dto.response;

import com.ceos.spring_cgv_23rd.domain.movie.domain.*;
import lombok.Builder;

import java.time.LocalDate;

public class MovieResponse {

    @Builder
    public record MovieListResponse(
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
    }

    @Builder
    public record MovieDetailResponse(
            Long id,
            String title,
            String prolog,
            MovieStatus status,
            Integer duration,
            Genre genre,
            AgeRating ageRating,
            LocalDate releasedAt,
            String posterUrl,
            MovieStatisticResponse statistic
    ) {
    }

    @Builder
    public record MovieCreditResponse(
            Long contributorId,
            String name,
            String profileImageUrl,
            RoleType roleType
    ) {
    }

    @Builder
    public record MovieMediaResponse(
            Long id,
            MediaType mediaType,
            String mediaUrl
    ) {
    }

    @Builder
    public record MovieLikeResponse(
            Long movieId,
            Boolean liked
    ) {
    }


    @Builder
    public record MovieStatisticResponse(
            Double reservationRate,
            Integer reservationRank,
            Long viewCount,
            Double eggCount,
            Double maleReservationRate,
            Double femaleReservationRate,
            AgeRateResponse ageRates
    ) {

    }

    @Builder
    public record AgeRateResponse(
            Double age10sRate,
            Double age20sRate,
            Double age30sRate,
            Double age40sRate,
            Double age50sRate
    ) {
    }
}
