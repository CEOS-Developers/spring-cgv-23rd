package com.ceos23.spring_boot.cgv.repository.store;

import com.ceos23.spring_boot.cgv.domain.store.CinemaMenuStock;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CinemaMenuStockRepository extends JpaRepository<CinemaMenuStock, Long> {
}