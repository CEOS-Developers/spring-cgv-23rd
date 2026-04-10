package com.ceos23.spring_boot.repository;

import com.ceos23.spring_boot.domain.TheaterItemStock;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TheaterItemStockRepository extends JpaRepository<TheaterItemStock, Long> {

    Optional<TheaterItemStock> findByTheaterIdAndItemId(Long theaterId, Long itemId);
}