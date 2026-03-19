package com.ceos23.cgv_clone.repository;

import com.ceos23.cgv_clone.domain.store.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {
}
