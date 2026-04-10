package com.ceos23.spring_boot.cgv.repository.like;

import com.ceos23.spring_boot.cgv.domain.like.MovieLike;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MovieLikeRepository extends JpaRepository<MovieLike, Long> {

    boolean existsByUserIdAndMovieId(Long userId, Long movieId);

    Optional<MovieLike> findByUserIdAndMovieId(Long userId, Long movieId);
}