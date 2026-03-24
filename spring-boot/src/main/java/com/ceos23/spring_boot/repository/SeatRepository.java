package com.ceos23.spring_boot.repository;

import com.ceos23.spring_boot.domain.Seat;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SeatRepository extends JpaRepository<Seat, Long> {
}