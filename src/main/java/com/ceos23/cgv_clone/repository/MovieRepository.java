package com.ceos23.cgv_clone.repository;

import com.ceos23.cgv_clone.domain.movie.Movie;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MovieRepository extends JpaRepository<Movie, Long> {
}
