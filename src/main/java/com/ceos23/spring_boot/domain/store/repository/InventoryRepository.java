package com.ceos23.spring_boot.domain.store.repository;

import com.ceos23.spring_boot.domain.store.entity.Inventory;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT i FROM Inventory i WHERE i.theater.id = :theaterId AND i.menu.id = :menuId")
    Optional<Inventory> findByTheaterIdAndMenuIdWithLock(
            @Param("theaterId") Long theaterId,
            @Param("menuId") Long menuId
    );
}
