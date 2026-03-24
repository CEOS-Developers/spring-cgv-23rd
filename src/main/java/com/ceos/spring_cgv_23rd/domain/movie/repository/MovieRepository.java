package com.ceos.spring_cgv_23rd.domain.movie.repository;

import com.ceos.spring_cgv_23rd.domain.movie.entity.Movie;
import com.ceos.spring_cgv_23rd.domain.movie.enums.MovieStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MovieRepository extends JpaRepository<Movie, Long> {

    @Query("SELECT m, ms FROM Movie m LEFT JOIN MovieStatistic ms ON ms.movie = m " +
            " WHERE m.status = :status ORDER BY ms.reservationRate DESC")
    List<Object[]> findWithStatisticByStatus(MovieStatus status);

    @Query("SELECT m, ms FROM Movie m LEFT JOIN MovieStatistic ms ON ms.movie = m " +
            " WHERE m.status IN :statuses ORDER BY ms.reservationRate DESC ")
    List<Object[]> findWithStatisticByStatusIn(List<MovieStatus> statuses);
}
