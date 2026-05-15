package com.ceos.spring_cgv_23rd.domain.movie.application.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ceos.spring_cgv_23rd.domain.movie.application.dto.result.MovieCreditResult;
import com.ceos.spring_cgv_23rd.domain.movie.application.dto.result.MovieDetailResult;
import com.ceos.spring_cgv_23rd.domain.movie.application.dto.result.MovieMediaResult;
import com.ceos.spring_cgv_23rd.domain.movie.application.dto.result.MovieResult;
import com.ceos.spring_cgv_23rd.domain.movie.application.port.in.GetMovieChartUseCase;
import com.ceos.spring_cgv_23rd.domain.movie.application.port.in.GetMovieDetailUseCase;
import com.ceos.spring_cgv_23rd.domain.movie.application.port.out.MoviePersistencePort;
import com.ceos.spring_cgv_23rd.domain.movie.domain.MovieStatus;
import com.ceos.spring_cgv_23rd.domain.movie.exception.MovieErrorCode;
import com.ceos.spring_cgv_23rd.global.apiPayload.exception.GeneralException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MovieQueryService implements GetMovieChartUseCase, GetMovieDetailUseCase {

	private final MoviePersistencePort moviePersistencePort;

	@Override
	public List<MovieResult> getMovieChart() {
		return moviePersistencePort.findMoviesWithStatisticByStatusIn(
				List.of(MovieStatus.RUNNING, MovieStatus.UPCOMING))
			.stream()
			.map(MovieResult::from)
			.toList();
	}

	@Override
	public List<MovieResult> getRunningMovies() {
		return moviePersistencePort.findMoviesWithStatisticByStatus(MovieStatus.RUNNING)
			.stream()
			.map(MovieResult::from)
			.toList();
	}

	@Override
	public List<MovieResult> getUpcomingMovies() {
		return moviePersistencePort.findMoviesWithStatisticByStatus(MovieStatus.UPCOMING)
			.stream()
			.map(MovieResult::from)
			.toList();
	}

	@Override
	public MovieDetailResult getMovieDetail(long movieId) {
		return moviePersistencePort.findMovieById(movieId)
			.map(MovieDetailResult::from)
			.orElseThrow(() -> new GeneralException(MovieErrorCode.MOVIE_NOT_FOUND));
	}

	@Override
	public List<MovieCreditResult> getMovieCredits(long movieId) {
		if (!moviePersistencePort.existsMovieById(movieId)) {
			throw new GeneralException(MovieErrorCode.MOVIE_NOT_FOUND);
		}

		return moviePersistencePort.findCreditsByMovieIdWithContributor(movieId)
			.stream()
			.map(MovieCreditResult::from)
			.toList();
	}

	@Override
	public List<MovieMediaResult> getMovieMedia(long movieId) {
		if (!moviePersistencePort.existsMovieById(movieId)) {
			throw new GeneralException(MovieErrorCode.MOVIE_NOT_FOUND);
		}

		return moviePersistencePort.findMediasByMovieId(movieId)
			.stream()
			.map(MovieMediaResult::from)
			.toList();
	}
}
