package com.ceos.spring_boot.domain.user.repository;

import com.ceos.spring_boot.domain.user.entity.CinemaLike;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CinemaLikeRepository extends JpaRepository<CinemaLike, Long> {

    List<CinemaLike> findByUserId(Long userId); // 유저가 찜한 영화관 조회
}
