package com.ceos.spring_cgv_23rd.domain.product.adapter.out.persistence.repository;


import com.ceos.spring_cgv_23rd.domain.product.adapter.out.persistence.entity.InventoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface InventoryJpaRepository extends JpaRepository<InventoryEntity, Long> {

    List<InventoryEntity> findAllByTheaterIdAndProductIdIn(Long theaterId, List<Long> productIds);

    @Modifying
    @Query("UPDATE InventoryEntity i " +
            "SET i.quantity = i.quantity - :count " +
            "WHERE i.theaterId = :theaterId " +
            "AND i.product.id = :productId " +
            "AND i.quantity >= :count")
    int decreaseQuantityIfEnough(Long theaterId, Long productId, int count);
}
