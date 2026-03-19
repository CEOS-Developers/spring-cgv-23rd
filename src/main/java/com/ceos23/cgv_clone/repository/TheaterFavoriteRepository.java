package com.ceos23.cgv_clone.repository;

import com.ceos23.cgv_clone.domain.favorite.TheaterFavorite;
import com.ceos23.cgv_clone.domain.theater.Theater;
import com.ceos23.cgv_clone.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TheaterFavoriteRepository extends JpaRepository<TheaterFavorite, Long> {
    boolean existsByUserAndTheater(User user, Theater theater);

    int countByUser(User user);
}
