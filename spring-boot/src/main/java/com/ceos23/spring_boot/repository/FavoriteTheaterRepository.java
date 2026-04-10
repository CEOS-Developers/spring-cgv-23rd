package com.ceos23.spring_boot.repository;

import com.ceos23.spring_boot.domain.FavoriteTheater;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FavoriteTheaterRepository extends JpaRepository<FavoriteTheater, Long> {

    boolean existsByUserIdAndTheaterId(Long userId, Long theaterId);

    void deleteByUserIdAndTheaterId(Long userId, Long theaterId);

    List<FavoriteTheater> findAllByUserId(Long userId);
}