package com.ceos.spring_boot.domain.like.repository;

import com.ceos.spring_boot.domain.like.entity.MovieLike;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MovieLikeRepository extends JpaRepository<MovieLike, Long> {

    List<MovieLike> findByUserId(Long userId); // 유저가 찜한 영화 조회

    boolean existsByUserIdAndMovieId(Long userId, Long movieId);

    Optional<MovieLike> findByUserIdAndMovieId(Long userId, Long movieId);
}
