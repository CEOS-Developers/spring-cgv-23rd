package com.ceos23.cgv_clone.theater.repository;

import com.ceos23.cgv_clone.theater.entity.Theater;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TheaterRepository extends JpaRepository<Theater, Long> {
    List<Theater> findAllByRegion(String region);
}
