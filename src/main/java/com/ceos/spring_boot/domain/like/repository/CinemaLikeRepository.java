package com.ceos.spring_boot.domain.like.repository;

import com.ceos.spring_boot.domain.like.entity.CinemaLike;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CinemaLikeRepository extends JpaRepository<CinemaLike, Long> {

    List<CinemaLike> findByUserId(Long userId); // 유저가 찜한 영화관 조회

    Optional<CinemaLike> findByUserIdAndCinemaId(Long userId, Long cinemaId);
}
