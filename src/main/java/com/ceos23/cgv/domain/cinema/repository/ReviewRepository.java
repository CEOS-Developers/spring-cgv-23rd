package com.ceos23.cgv.domain.cinema.repository;

import com.ceos23.cgv.domain.cinema.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    // 특정 영화(movieId)에 달린 리뷰들만 가져오는 메서드
    List<Review> findByMovieId(Long movieId);
}