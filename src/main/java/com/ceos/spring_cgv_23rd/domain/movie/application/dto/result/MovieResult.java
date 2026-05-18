package com.ceos.spring_cgv_23rd.domain.movie.application.dto.result;

import java.time.LocalDate;

import com.ceos.spring_cgv_23rd.domain.movie.domain.AgeRating;
import com.ceos.spring_cgv_23rd.domain.movie.domain.Movie;
import com.ceos.spring_cgv_23rd.domain.movie.domain.MovieStatistic;
import com.ceos.spring_cgv_23rd.domain.movie.domain.MovieStatus;

public record MovieResult(
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

	public static MovieResult from(Movie movie) {
		MovieStatistic stat = movie.getMovieStatistic();
		return new MovieResult(
			movie.getId(),
			movie.getTitle(),
			movie.getPosterUrl(),
			movie.getStatus(),
			movie.getAgeRating(),
			stat.getReservationRate(),
			stat.getReservationRank(),
			stat.getViewCount(),
			stat.getEggCount(),
			movie.getReleasedAt()
		);
	}
}
