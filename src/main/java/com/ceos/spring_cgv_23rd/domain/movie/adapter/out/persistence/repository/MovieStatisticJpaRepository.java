package com.ceos.spring_cgv_23rd.domain.movie.adapter.out.persistence.repository;

import com.ceos.spring_cgv_23rd.domain.movie.adapter.out.persistence.entity.MovieStatisticEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MovieStatisticJpaRepository extends JpaRepository<MovieStatisticEntity, Long> {

    Optional<MovieStatisticEntity> findByMovieId(Long movieId);
}
