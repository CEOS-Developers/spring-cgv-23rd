package com.ceos.spring_cgv_23rd.domain.movie.repository;

import com.ceos.spring_cgv_23rd.domain.movie.entity.Movie;
import com.ceos.spring_cgv_23rd.domain.movie.enums.MovieStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MovieRepository extends JpaRepository<Movie, Long> {


    @Query("SELECT m FROM Movie m JOIN FETCH m.movieStatistic " +
            "WHERE m.status = :status ORDER BY m.movieStatistic.reservationRate DESC")
    List<Movie> findWithStatisticByStatus(MovieStatus status);

    
    @Query("SELECT m FROM Movie m JOIN FETCH m.movieStatistic " +
            "WHERE m.status IN :statuses ORDER BY m.movieStatistic.reservationRate DESC")
    List<Movie> findWithStatisticByStatusIn(List<MovieStatus> statuses);
}
