package com.ceos23.spring_boot.cgv.repository.store;

import com.ceos23.spring_boot.cgv.domain.store.StorePurchase;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StorePurchaseRepository extends JpaRepository<StorePurchase, Long> {
}