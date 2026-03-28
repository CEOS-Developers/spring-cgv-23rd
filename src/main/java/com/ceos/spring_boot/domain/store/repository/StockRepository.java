package com.ceos.spring_boot.domain.store.repository;

import com.ceos.spring_boot.domain.store.entity.Stock;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StockRepository extends JpaRepository<Stock, Long> {

    Optional<Stock> findByCinemaIdAndProductId(Long cinemaId, Long productId);
}