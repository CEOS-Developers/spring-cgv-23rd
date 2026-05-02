package com.ceos23.spring_boot.cgv.repository.store;

import com.ceos23.spring_boot.cgv.domain.store.StorePurchase;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;

public interface StorePurchaseRepository extends JpaRepository<StorePurchase, Long> {

    @EntityGraph(attributePaths = {"cinemaMenuStock", "cinemaMenuStock.cinema", "cinemaMenuStock.storeMenu"})
    List<StorePurchase> findAllByUserIdOrderByPurchasedAtDesc(Long userId);
}
