package com.ceos23.cgv_clone.movie.repository;

import com.ceos23.cgv_clone.movie.domain.Movie;
import com.ceos23.cgv_clone.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MovieRepository extends JpaRepository<Movie, Long> {
    static boolean existsByUserAndMovie(User user, Movie movie) {
    }
}
