package com.ceos.spring_cgv_23rd.domain.movie.adapter.out.persistence.repository;

import com.ceos.spring_cgv_23rd.domain.movie.adapter.out.persistence.entity.MovieMediaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MovieMediaJpaRepository extends JpaRepository<MovieMediaEntity, Long> {

    List<MovieMediaEntity> findByMovieId(Long movieId);
}
