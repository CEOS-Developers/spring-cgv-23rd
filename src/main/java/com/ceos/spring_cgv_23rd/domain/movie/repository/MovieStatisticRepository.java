package com.ceos.spring_cgv_23rd.domain.movie.repository;

import com.ceos.spring_cgv_23rd.domain.movie.entity.MovieStatistic;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MovieStatisticRepository extends JpaRepository<MovieStatistic, Long> {

    Optional<MovieStatistic> findByMovieId(Long movieId);
}
