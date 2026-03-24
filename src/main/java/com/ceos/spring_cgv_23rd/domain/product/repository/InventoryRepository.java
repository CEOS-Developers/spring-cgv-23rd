package com.ceos.spring_cgv_23rd.domain.product.repository;

import com.ceos.spring_cgv_23rd.domain.product.entity.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {

    List<Inventory> findAllByTheaterIdAndProductIdIn(Long theaterId, List<Long> productIds);
}
