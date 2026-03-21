package com.ceos23.spring_boot.cgv.repository.movie;

import com.ceos23.spring_boot.cgv.domain.movie.Movie;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MovieRepository extends JpaRepository<Movie, Long> {
}