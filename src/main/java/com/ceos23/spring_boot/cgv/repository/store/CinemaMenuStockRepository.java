package com.ceos23.spring_boot.cgv.repository.store;

import com.ceos23.spring_boot.cgv.domain.store.CinemaMenuStock;
import jakarta.persistence.LockModeType;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CinemaMenuStockRepository extends JpaRepository<CinemaMenuStock, Long> {

    Optional<CinemaMenuStock> findByCinemaIdAndStoreMenuId(Long cinemaId, Long storeMenuId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            select stock
            from CinemaMenuStock stock
            where stock.cinema.id = :cinemaId
              and stock.storeMenu.id = :storeMenuId
            """)
    Optional<CinemaMenuStock> findByCinemaIdAndStoreMenuIdWithPessimisticLock(
            @Param("cinemaId") Long cinemaId,
            @Param("storeMenuId") Long storeMenuId
    );

    @EntityGraph(attributePaths = {"cinema", "storeMenu"})
    List<CinemaMenuStock> findAllByCinemaIdOrderByStoreMenuNameAsc(Long cinemaId);
}
