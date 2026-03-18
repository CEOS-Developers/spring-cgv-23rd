package com.cgv.spring_boot.domain.movie.repository;

import com.cgv.spring_boot.domain.movie.entity.MovieWish;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MovieWishRepository extends JpaRepository<MovieWish, Long> {
}
