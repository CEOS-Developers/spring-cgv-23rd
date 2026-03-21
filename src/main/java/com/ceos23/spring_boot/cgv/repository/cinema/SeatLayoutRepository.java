package com.ceos23.spring_boot.cgv.repository.cinema;

import com.ceos23.spring_boot.cgv.domain.cinema.SeatLayout;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SeatLayoutRepository extends JpaRepository<SeatLayout, Long> {
}