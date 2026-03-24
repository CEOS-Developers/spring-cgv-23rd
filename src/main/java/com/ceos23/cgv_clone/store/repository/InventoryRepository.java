package com.ceos23.cgv_clone.store.repository;

import com.ceos23.cgv_clone.store.domain.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {
}
