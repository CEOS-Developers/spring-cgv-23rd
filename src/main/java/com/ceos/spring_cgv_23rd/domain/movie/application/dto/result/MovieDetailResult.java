package com.ceos.spring_cgv_23rd.domain.movie.application.dto.result;

import java.time.LocalDate;

import com.ceos.spring_cgv_23rd.domain.movie.domain.AgeRating;
import com.ceos.spring_cgv_23rd.domain.movie.domain.Genre;
import com.ceos.spring_cgv_23rd.domain.movie.domain.Movie;
import com.ceos.spring_cgv_23rd.domain.movie.domain.MovieStatus;

public record MovieDetailResult(
	Long id,
	String title,
	String prolog,
	MovieStatus status,
	Integer duration,
	Genre genre,
	AgeRating ageRating,
	LocalDate releasedAt,
	String posterUrl,
	MovieStatisticResult statistic
) {
	public static MovieDetailResult from(Movie movie) {
		return new MovieDetailResult(
			movie.getId(),
			movie.getTitle(),
			movie.getProlog(),
			movie.getStatus(),
			movie.getDuration(),
			movie.getGenre(),
			movie.getAgeRating(),
			movie.getReleasedAt(),
			movie.getPosterUrl(),
			MovieStatisticResult.from(movie.getMovieStatistic())
		);
	}
}
