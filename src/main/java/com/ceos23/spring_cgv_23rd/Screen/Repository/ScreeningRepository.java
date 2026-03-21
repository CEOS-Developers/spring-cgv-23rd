package com.ceos23.spring_cgv_23rd.Screen.Repository;

import com.ceos23.spring_cgv_23rd.Screen.Domain.Screening;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScreeningRepository extends JpaRepository<Screening, Long> {
}
