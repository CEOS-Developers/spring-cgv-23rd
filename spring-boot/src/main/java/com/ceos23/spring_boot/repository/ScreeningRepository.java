package com.ceos23.spring_boot.repository;

import com.ceos23.spring_boot.domain.Screening;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScreeningRepository extends JpaRepository<Screening, Long> {
}