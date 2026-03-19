package com.ceos23.cgv_clone.repository;

import com.ceos23.cgv_clone.domain.movie.Review;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Review, Long> {
}
