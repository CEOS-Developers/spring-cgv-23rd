package com.ceos23.spring_boot.domain.user.repository;

import com.ceos23.spring_boot.domain.movie.entity.Movie;
import com.ceos23.spring_boot.domain.theater.entity.Theater;
import com.ceos23.spring_boot.domain.user.entity.FavoriteMovie;
import com.ceos23.spring_boot.domain.user.entity.FavoriteTheater;
import com.ceos23.spring_boot.domain.user.entity.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FavoriteMovieRepository extends JpaRepository<FavoriteMovie, Long> {
    Optional<FavoriteMovie> findByUserAndMovie(User user, Movie movie);

    @EntityGraph(attributePaths = "movie")
    List<FavoriteMovie> findAllByUserEmail(String email);
}
