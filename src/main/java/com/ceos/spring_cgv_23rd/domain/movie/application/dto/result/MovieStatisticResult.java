package com.ceos.spring_cgv_23rd.domain.movie.application.dto.result;

import com.ceos.spring_cgv_23rd.domain.movie.domain.MovieStatistic;

public record MovieStatisticResult(
	Double reservationRate,
	Integer reservationRank,
	Long viewCount,
	Double eggCount,
	Double maleReservationRate,
	Double femaleReservationRate,
	Double age10sRate,
	Double age20sRate,
	Double age30sRate,
	Double age40sRate,
	Double age50sRate
) {
	public static MovieStatisticResult from(MovieStatistic stat) {
		return new MovieStatisticResult(
			stat.getReservationRate(),
			stat.getReservationRank(),
			stat.getViewCount(),
			stat.getEggCount(),
			stat.getMaleReservationRate(),
			stat.getFemaleReservationRate(),
			stat.getAge10sRate(),
			stat.getAge20sRate(),
			stat.getAge30sRate(),
			stat.getAge40sRate(),
			stat.getAge50sRate()
		);
	}
}
