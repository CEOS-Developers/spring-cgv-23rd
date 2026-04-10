package com.ceos23.cgv_clone.store.repository;

import com.ceos23.cgv_clone.store.domain.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {
    List<Inventory> findAllByStore_Id(Long storeId);
}
