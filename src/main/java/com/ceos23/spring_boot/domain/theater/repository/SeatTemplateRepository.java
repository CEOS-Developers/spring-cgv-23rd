package com.ceos23.spring_boot.domain.theater.repository;

import com.ceos23.spring_boot.domain.theater.entity.SeatTemplate;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SeatTemplateRepository extends JpaRepository<SeatTemplate, Long> {
}
