package com.ceos23.cgv.domain.concession.repository;

import com.ceos23.cgv.domain.concession.entity.Inventory;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {
    // 1. 특정 극장(지점)의 모든 매점 상품 재고 목록 조회
    List<Inventory> findByCinemaId(Long cinemaId);

    // 2. 극장과 상품 조합으로 특정 재고 데이터 단건 조회 (업데이트 시 필요)
    Optional<Inventory> findByCinemaIdAndProductId(Long cinemaId, Long productId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT i FROM Inventory i " +
            "WHERE i.cinema.id = :cinemaId AND i.product.id = :productId")
    Optional<Inventory> findByCinemaIdAndProductIdForUpdate(@Param("cinemaId") Long cinemaId,
                                                            @Param("productId") Long productId);
}
