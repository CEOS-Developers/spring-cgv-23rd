package com.ceos23.cgv_clone.movie.repository;

import com.ceos23.cgv_clone.movie.entity.Movie;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MovieRepository extends JpaRepository<Movie, Long> {
}
