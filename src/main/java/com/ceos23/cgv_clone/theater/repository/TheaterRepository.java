package com.ceos23.cgv_clone.theater.repository;

import com.ceos23.cgv_clone.theater.domain.Theater;
import com.ceos23.cgv_clone.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TheaterRepository extends JpaRepository<Theater, Long> {
    List<Theater> findAllByRegion(String region);

    boolean existsByUserAndTheater(User user, Theater theater);
}
