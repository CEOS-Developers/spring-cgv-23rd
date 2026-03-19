package com.ceos23.cgv_clone.repository;

import com.ceos23.cgv_clone.domain.favorite.MovieFavorite;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MovieFavoriteRepository extends JpaRepository<MovieFavorite, Long> {
}
