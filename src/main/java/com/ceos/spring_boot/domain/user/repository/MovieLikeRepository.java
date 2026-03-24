package com.ceos.spring_boot.domain.user.repository;

import com.ceos.spring_boot.domain.user.entity.MovieLike;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MovieLikeRepository extends JpaRepository<MovieLike, Long> {

    List<MovieLike> findByUserId(Long userId); // 유저가 찜한 영화 조회
}
