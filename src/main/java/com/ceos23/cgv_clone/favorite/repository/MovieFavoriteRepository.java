package com.ceos23.cgv_clone.favorite.repository;

import com.ceos23.cgv_clone.favorite.domain.MovieFavorite;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MovieFavoriteRepository extends JpaRepository<MovieFavorite, Long> {
}
