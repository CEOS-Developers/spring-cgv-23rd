package com.ceos23.spring_boot.repository;

import com.ceos23.spring_boot.domain.Movie;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MovieRepository extends JpaRepository<Movie, Long> {
}