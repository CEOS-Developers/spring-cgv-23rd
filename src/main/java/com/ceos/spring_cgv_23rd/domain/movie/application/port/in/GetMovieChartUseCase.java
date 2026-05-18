package com.ceos.spring_cgv_23rd.domain.movie.application.port.in;

import java.util.List;

import com.ceos.spring_cgv_23rd.domain.movie.application.dto.result.MovieResult;

public interface GetMovieChartUseCase {

	List<MovieResult> getMovieChart();

	List<MovieResult> getRunningMovies();

	List<MovieResult> getUpcomingMovies();
}
