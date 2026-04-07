package com.ceos.spring_cgv_23rd.domain.movie.application.service;

import com.ceos.spring_cgv_23rd.domain.movie.application.port.in.GetMovieChartUseCase;
import com.ceos.spring_cgv_23rd.domain.movie.application.port.in.GetMovieDetailUseCase;
import com.ceos.spring_cgv_23rd.domain.movie.application.port.out.MoviePersistencePort;
import com.ceos.spring_cgv_23rd.domain.movie.domain.Movie;
import com.ceos.spring_cgv_23rd.domain.movie.domain.MovieCredit;
import com.ceos.spring_cgv_23rd.domain.movie.domain.MovieMedia;
import com.ceos.spring_cgv_23rd.domain.movie.domain.MovieStatus;
import com.ceos.spring_cgv_23rd.domain.movie.exception.MovieErrorCode;
import com.ceos.spring_cgv_23rd.global.apiPayload.exception.GeneralException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MovieQueryService implements GetMovieChartUseCase, GetMovieDetailUseCase {

    private final MoviePersistencePort moviePersistencePort;


    @Override
    public List<Movie> getMovieChart() {
        return moviePersistencePort.findMoviesWithStatisticByStatusIn(List.of(MovieStatus.RUNNING, MovieStatus.UPCOMING));
    }

    @Override
    public List<Movie> getRunningMovies() {
        return moviePersistencePort.findMoviesWithStatisticByStatus(MovieStatus.RUNNING);
    }

    @Override
    public List<Movie> getUpcomingMovies() {
        return moviePersistencePort.findMoviesWithStatisticByStatus(MovieStatus.UPCOMING);
    }


    @Override
    public Movie getMovieDetail(long movieId) {
        return moviePersistencePort.findMovieById(movieId)
                .orElseThrow(() -> new GeneralException(MovieErrorCode.MOVIE_NOT_FOUND));
    }

    @Override
    public List<MovieCredit> getMovieCredits(long movieId) {
        if (!moviePersistencePort.existsMovieById(movieId)) {
            throw new GeneralException(MovieErrorCode.MOVIE_NOT_FOUND);
        }

        return moviePersistencePort.findCreditsByMovieIdWithContributor(movieId);
    }

    @Override
    public List<MovieMedia> getMovieMedia(long movieId) {
        if (!moviePersistencePort.existsMovieById(movieId)) {
            throw new GeneralException(MovieErrorCode.MOVIE_NOT_FOUND);
        }

        return moviePersistencePort.findMediasByMovieId(movieId);
    }
}
