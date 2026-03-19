package com.ceos23.cgv_clone.repository;

import com.ceos23.cgv_clone.domain.favorite.TheaterFavorite;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TheaterFavoriteRepository extends JpaRepository<TheaterFavorite, Long> {
}
