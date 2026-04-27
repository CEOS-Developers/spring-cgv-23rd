package com.ceos23.spring_boot.repository;

import com.ceos23.spring_boot.domain.FavoriteMovie;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FavoriteMovieRepository extends JpaRepository<FavoriteMovie, Long> {

    boolean existsByUserIdAndMovieId(Long userId, Long movieId);

    void deleteByUserIdAndMovieId(Long userId, Long movieId);

    List<FavoriteMovie> findAllByUserId(Long userId);

    Optional<FavoriteMovie> findByUserIdAndMovieId(Long userId, Long movieId);
}