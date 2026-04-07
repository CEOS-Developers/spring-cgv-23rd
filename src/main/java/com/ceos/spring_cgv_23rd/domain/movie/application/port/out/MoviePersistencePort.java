package com.ceos.spring_cgv_23rd.domain.movie.application.port.out;

import com.ceos.spring_cgv_23rd.domain.movie.domain.*;

import java.util.List;
import java.util.Optional;

public interface MoviePersistencePort {

    // Movie
    Optional<Movie> findMovieById(Long movieId);

    boolean existsMovieById(Long movieId);

    List<Movie> findMoviesWithStatisticByStatus(MovieStatus status);

    List<Movie> findMoviesWithStatisticByStatusIn(List<MovieStatus> statuses);


    // MovieCredit
    List<MovieCredit> findCreditsByMovieIdWithContributor(Long movieId);


    // MovieMedia
    List<MovieMedia> findMediasByMovieId(Long movieId);


    // MovieLike
    Optional<MovieLike> findMovieLikeByUserAndMovie(Long userId, Long movieId);

    MovieLike saveMovieLike(MovieLike movieLike);

    void deleteMovieLike(MovieLike movieLike);
    
}
