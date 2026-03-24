package com.ceos.spring_cgv_23rd.domain.movie.repository;

import com.ceos.spring_cgv_23rd.domain.movie.entity.MovieLike;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MovieLikeRepository extends JpaRepository<MovieLike, Long> {

    Optional<MovieLike> findByUserIdAndMovieId(Long userId, Long movieId);
}
