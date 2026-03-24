package com.ceos23.spring_boot.cgv.repository.like;

import com.ceos23.spring_boot.cgv.domain.like.MovieLike;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MovieLikeRepository extends JpaRepository<MovieLike, Long> {
}