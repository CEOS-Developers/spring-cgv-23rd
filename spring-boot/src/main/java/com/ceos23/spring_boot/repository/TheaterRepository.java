package com.ceos23.spring_boot.repository;

import com.ceos23.spring_boot.domain.Theater;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TheaterRepository extends JpaRepository<Theater, Long> {
}