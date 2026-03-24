package com.ceos23.spring_cgv_23rd.Movie.Repository;

import com.ceos23.spring_cgv_23rd.Movie.Domain.Movie;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MovieRepository extends JpaRepository<Movie, Long> {
    Optional<Movie> findByMovieName(String movieName);

    List<Movie> findByMovieNameContaining(String query);
}
