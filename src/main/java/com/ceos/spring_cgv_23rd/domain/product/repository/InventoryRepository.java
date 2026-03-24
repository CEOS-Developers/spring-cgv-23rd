package com.ceos.spring_cgv_23rd.domain.product.repository;

import com.ceos.spring_cgv_23rd.domain.product.entity.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {

    Optional<Inventory> findByTheaterIdAndProductId(Long theaterId, Long productId);
}
