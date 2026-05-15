package com.ceos.spring_cgv_23rd.domain.movie.application.port.in;

import java.util.List;

import com.ceos.spring_cgv_23rd.domain.movie.application.dto.result.MovieCreditResult;
import com.ceos.spring_cgv_23rd.domain.movie.application.dto.result.MovieDetailResult;
import com.ceos.spring_cgv_23rd.domain.movie.application.dto.result.MovieMediaResult;

public interface GetMovieDetailUseCase {

	MovieDetailResult getMovieDetail(long movieId);

	List<MovieCreditResult> getMovieCredits(long movieId);

	List<MovieMediaResult> getMovieMedia(long movieId);
}
