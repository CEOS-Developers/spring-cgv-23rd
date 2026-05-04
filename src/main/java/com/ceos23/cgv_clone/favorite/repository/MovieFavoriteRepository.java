package com.ceos23.cgv_clone.favorite.repository;

import com.ceos23.cgv_clone.favorite.entity.MovieFavorite;
import com.ceos23.cgv_clone.movie.entity.Movie;
import com.ceos23.cgv_clone.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MovieFavoriteRepository extends JpaRepository<MovieFavorite, Long> {
    Optional<MovieFavorite> findByUserAndMovie(User user, Movie movie);
}
