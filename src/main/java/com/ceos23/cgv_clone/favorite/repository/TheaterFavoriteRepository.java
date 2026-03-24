package com.ceos23.cgv_clone.favorite.repository;

import com.ceos23.cgv_clone.favorite.domain.TheaterFavorite;
import com.ceos23.cgv_clone.theater.domain.Theater;
import com.ceos23.cgv_clone.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TheaterFavoriteRepository extends JpaRepository<TheaterFavorite, Long> {
    boolean existsByUserAndTheater(User user, Theater theater);

    void deleteByUserAndTheater(User user, Theater theater);

    int countByUser(User user);
}
