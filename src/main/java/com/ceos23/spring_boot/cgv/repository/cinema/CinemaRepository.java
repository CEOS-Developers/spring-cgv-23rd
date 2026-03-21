package com.ceos23.spring_boot.cgv.repository.cinema;

import com.ceos23.spring_boot.cgv.domain.cinema.Cinema;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CinemaRepository extends JpaRepository<Cinema, Long> {
}