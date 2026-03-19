package com.ceos23.cgv_clone.repository;

import com.ceos23.cgv_clone.domain.movie.MovieStatistic;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MovieStatisticRepository extends JpaRepository<MovieStatistic, Long> {
}
