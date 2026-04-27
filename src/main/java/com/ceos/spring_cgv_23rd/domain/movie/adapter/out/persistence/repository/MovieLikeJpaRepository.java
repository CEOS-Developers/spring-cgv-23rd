package com.ceos.spring_cgv_23rd.domain.movie.adapter.out.persistence.repository;

import com.ceos.spring_cgv_23rd.domain.movie.adapter.out.persistence.entity.MovieLikeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MovieLikeJpaRepository extends JpaRepository<MovieLikeEntity, Long> {

    Optional<MovieLikeEntity> findByUserIdAndMovieId(Long userId, Long movieId);
}
