package com.ceos23.spring_boot.domain.theater.repository;

import com.ceos23.spring_boot.domain.theater.entity.Screen;
import com.ceos23.spring_boot.domain.theater.entity.Theater;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ScreenRepository extends JpaRepository<Screen, Long> {
    Optional<Screen> findByNameAndTheater(String name, Theater theater);
}
