package com.ceos.spring_cgv_23rd.domain.movie.application.port.in;

import com.ceos.spring_cgv_23rd.domain.movie.domain.Movie;
import com.ceos.spring_cgv_23rd.domain.movie.domain.MovieCredit;
import com.ceos.spring_cgv_23rd.domain.movie.domain.MovieMedia;

import java.util.List;

public interface GetMovieDetailUseCase {

    Movie getMovieDetail(long movieId);

    List<MovieCredit> getMovieCredits(long movieId);

    List<MovieMedia> getMovieMedia(long movieId);
}
