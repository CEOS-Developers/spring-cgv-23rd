package com.ceos23.spring_boot.cgv.repository.store;

import com.ceos23.spring_boot.cgv.domain.store.CinemaMenuStock;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;

public interface CinemaMenuStockRepository extends JpaRepository<CinemaMenuStock, Long> {

    Optional<CinemaMenuStock> findByCinemaIdAndStoreMenuId(Long cinemaId, Long storeMenuId);

    @EntityGraph(attributePaths = {"cinema", "storeMenu"})
    List<CinemaMenuStock> findAllByCinemaIdOrderByStoreMenuNameAsc(Long cinemaId);
}
