package com.ceos23.spring_boot.repository;

import com.ceos23.spring_boot.domain.Screen;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScreenRepository extends JpaRepository<Screen, Long> {
}