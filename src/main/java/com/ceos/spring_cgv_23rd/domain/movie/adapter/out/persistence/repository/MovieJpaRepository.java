package com.ceos.spring_cgv_23rd.domain.movie.adapter.out.persistence.repository;

import com.ceos.spring_cgv_23rd.domain.movie.adapter.out.persistence.entity.MovieEntity;
import com.ceos.spring_cgv_23rd.domain.movie.domain.MovieStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface MovieJpaRepository extends JpaRepository<MovieEntity, Long> {

    @Query("SELECT m FROM MovieEntity m JOIN FETCH m.movieStatistic WHERE m.id = :movieId")
    Optional<MovieEntity> findWithStatisticById(Long movieId);

    @Query("SELECT m FROM MovieEntity m JOIN FETCH m.movieStatistic " +
            "WHERE m.status = :status ORDER BY m.movieStatistic.reservationRate DESC")
    List<MovieEntity> findWithStatisticByStatus(MovieStatus status);


    @Query("SELECT m FROM MovieEntity m JOIN FETCH m.movieStatistic " +
            "WHERE m.status IN :statuses ORDER BY m.movieStatistic.reservationRate DESC")
    List<MovieEntity> findWithStatisticByStatusIn(List<MovieStatus> statuses);
}
