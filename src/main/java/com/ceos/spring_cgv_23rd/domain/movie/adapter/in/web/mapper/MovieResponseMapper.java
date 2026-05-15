package com.ceos.spring_cgv_23rd.domain.movie.adapter.in.web.mapper;

import java.util.List;

import org.springframework.stereotype.Component;

import com.ceos.spring_cgv_23rd.domain.movie.adapter.in.web.dto.response.MovieResponse;
import com.ceos.spring_cgv_23rd.domain.movie.application.dto.result.MovieCreditResult;
import com.ceos.spring_cgv_23rd.domain.movie.application.dto.result.MovieDetailResult;
import com.ceos.spring_cgv_23rd.domain.movie.application.dto.result.MovieMediaResult;
import com.ceos.spring_cgv_23rd.domain.movie.application.dto.result.MovieResult;
import com.ceos.spring_cgv_23rd.domain.movie.application.dto.result.MovieStatisticResult;
import com.ceos.spring_cgv_23rd.domain.movie.application.dto.result.ToggleMovieLikeResult;

@Component
public class MovieResponseMapper {

	public List<MovieResponse.MovieListResponse> toMovieListResponse(List<MovieResult> results) {
		return results.stream()
			.map(this::toMovieListDto)
			.toList();
	}

	public MovieResponse.MovieDetailResponse toMovieDetailResponse(MovieDetailResult result) {
		return MovieResponse.MovieDetailResponse.builder()
			.id(result.id())
			.title(result.title())
			.prolog(result.prolog())
			.status(result.status())
			.duration(result.duration())
			.genre(result.genre())
			.ageRating(result.ageRating())
			.releasedAt(result.releasedAt())
			.posterUrl(result.posterUrl())
			.statistic(toStatisticDto(result.statistic()))
			.build();
	}

	public List<MovieResponse.MovieCreditResponse> toMovieCreditResponse(List<MovieCreditResult> results) {
		return results.stream()
			.map(credit -> MovieResponse.MovieCreditResponse.builder()
				.contributorId(credit.contributorId())
				.name(credit.name())
				.profileImageUrl(credit.profileImageUrl())
				.roleType(credit.roleType())
				.build())
			.toList();
	}

	public List<MovieResponse.MovieMediaResponse> toMovieMediaResponse(List<MovieMediaResult> results) {
		return results.stream()
			.map(media -> MovieResponse.MovieMediaResponse.builder()
				.id(media.id())
				.mediaType(media.mediaType())
				.mediaUrl(media.mediaUrl())
				.build())
			.toList();
	}

	public MovieResponse.MovieLikeResponse toMovieLikeResponse(ToggleMovieLikeResult result) {
		return MovieResponse.MovieLikeResponse.builder()
			.movieId(result.movieId())
			.liked(result.liked())
			.build();
	}

	private MovieResponse.MovieListResponse toMovieListDto(MovieResult result) {
		return MovieResponse.MovieListResponse.builder()
			.id(result.id())
			.title(result.title())
			.posterUrl(result.posterUrl())
			.status(result.status())
			.ageRating(result.ageRating())
			.reservationRate(result.reservationRate())
			.reservationRank(result.reservationRank())
			.viewCount(result.viewCount())
			.eggCount(result.eggCount())
			.releasedAt(result.releasedAt())
			.build();
	}

	private MovieResponse.MovieStatisticResponse toStatisticDto(MovieStatisticResult result) {
		return MovieResponse.MovieStatisticResponse.builder()
			.reservationRate(result.reservationRate())
			.reservationRank(result.reservationRank())
			.viewCount(result.viewCount())
			.eggCount(result.eggCount())
			.maleReservationRate(result.maleReservationRate())
			.femaleReservationRate(result.femaleReservationRate())
			.ageRates(toAgeRateDto(result))
			.build();
	}

	private MovieResponse.AgeRateResponse toAgeRateDto(MovieStatisticResult result) {
		return MovieResponse.AgeRateResponse.builder()
			.age10sRate(result.age10sRate())
			.age20sRate(result.age20sRate())
			.age30sRate(result.age30sRate())
			.age40sRate(result.age40sRate())
			.age50sRate(result.age50sRate())
			.build();
	}
}
