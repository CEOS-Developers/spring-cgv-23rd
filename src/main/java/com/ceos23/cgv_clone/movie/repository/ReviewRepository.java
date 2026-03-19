package com.ceos23.cgv_clone.movie.repository;

import com.ceos23.cgv_clone.movie.domain.Review;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Review, Long> {
}
