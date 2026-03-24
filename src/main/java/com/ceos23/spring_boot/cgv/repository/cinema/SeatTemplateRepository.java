package com.ceos23.spring_boot.cgv.repository.cinema;

import com.ceos23.spring_boot.cgv.domain.cinema.SeatTemplate;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SeatTemplateRepository extends JpaRepository<SeatTemplate, Long> {
}