package com.ceos23.cgv_clone.favorite.repository;

import com.ceos23.cgv_clone.favorite.entity.MovieFavorite;
import com.ceos23.cgv_clone.movie.entity.Movie;
import com.ceos23.cgv_clone.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MovieFavoriteRepository extends JpaRepository<MovieFavorite, Long> {
    void deleteByUserAndMovie(User user, Movie movie);

    boolean existsByUserAndMovie(User user, Movie movie);
}
