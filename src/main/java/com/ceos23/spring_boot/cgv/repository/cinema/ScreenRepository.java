package com.ceos23.spring_boot.cgv.repository.cinema;

import com.ceos23.spring_boot.cgv.domain.cinema.Screen;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScreenRepository extends JpaRepository<Screen, Long> {
}