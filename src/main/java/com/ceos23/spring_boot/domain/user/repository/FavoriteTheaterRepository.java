package com.ceos23.spring_boot.domain.user.repository;

import com.ceos23.spring_boot.domain.theater.entity.Theater;
import com.ceos23.spring_boot.domain.user.entity.FavoriteTheater;
import com.ceos23.spring_boot.domain.user.entity.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FavoriteTheaterRepository extends JpaRepository<FavoriteTheater, Long> {
    Optional<FavoriteTheater> findByUserAndTheater(User user, Theater theater);

    @EntityGraph(attributePaths = "theater")
    List<FavoriteTheater> findAllByUserEmail(String email);
}
