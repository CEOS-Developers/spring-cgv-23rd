package com.ceos.spring_cgv_23rd.domain.theater.repository;

import com.ceos.spring_cgv_23rd.domain.theater.entity.Theater;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TheaterRepository extends JpaRepository<Theater, Long> {
}
