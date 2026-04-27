package com.ceos.spring_cgv_23rd.domain.movie.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;


@Getter
@Builder
@AllArgsConstructor
public class MovieStatistic {

    private final Long id;
    private final Double reservationRate;
    private final Integer reservationRank;
    private final Long viewCount;
    private final Double eggCount;
    private final Double maleReservationRate;
    private final Double femaleReservationRate;
    private final Double age10sRate;
    private final Double age20sRate;
    private final Double age30sRate;
    private final Double age40sRate;
    private final Double age50sRate;

}
