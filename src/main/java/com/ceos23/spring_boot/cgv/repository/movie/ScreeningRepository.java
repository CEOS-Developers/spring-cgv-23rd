package com.ceos23.spring_boot.cgv.repository.movie;

import com.ceos23.spring_boot.cgv.domain.movie.Screening;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScreeningRepository extends JpaRepository<Screening, Long> {
}