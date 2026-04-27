package com.ceos.spring_cgv_23rd.domain.movie.application.port.in;

import com.ceos.spring_cgv_23rd.domain.movie.domain.Movie;

import java.util.List;

public interface GetMovieChartUseCase {

    List<Movie> getMovieChart();

    List<Movie> getRunningMovies();

    List<Movie> getUpcomingMovies();
}
