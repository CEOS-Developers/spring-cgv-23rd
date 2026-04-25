package com.ceos23.spring_boot.repository;

import com.ceos23.spring_boot.domain.TheaterItemStock;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import java.util.Optional;

public interface TheaterItemStockRepository extends JpaRepository<TheaterItemStock, Long> {

    Optional<TheaterItemStock> findByTheaterIdAndItemId(Long theaterId, Long itemId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<TheaterItemStock> findWithLockByTheaterIdAndItemId(Long theaterId, Long itemId);
}