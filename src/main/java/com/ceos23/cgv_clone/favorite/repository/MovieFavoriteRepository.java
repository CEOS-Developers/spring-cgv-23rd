package com.ceos23.cgv_clone.favorite.repository;

import com.ceos23.cgv_clone.favorite.domain.MovieFavorite;
import com.ceos23.cgv_clone.movie.domain.Movie;
import com.ceos23.cgv_clone.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MovieFavoriteRepository extends JpaRepository<MovieFavorite, Long> {
    void deleteByUserAndMovie(User user, Movie movie);
}
