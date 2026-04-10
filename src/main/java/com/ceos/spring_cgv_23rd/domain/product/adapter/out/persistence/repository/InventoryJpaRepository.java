package com.ceos.spring_cgv_23rd.domain.product.adapter.out.persistence.repository;


import com.ceos.spring_cgv_23rd.domain.product.adapter.out.persistence.entity.InventoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InventoryJpaRepository extends JpaRepository<InventoryEntity, Long> {

    List<InventoryEntity> findAllByTheaterIdAndProductIdIn(Long theaterId, List<Long> productIds);
}
