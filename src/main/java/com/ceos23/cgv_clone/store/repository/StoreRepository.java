package com.ceos23.cgv_clone.store.repository;

import com.ceos23.cgv_clone.store.domain.Store;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StoreRepository extends JpaRepository<Store, Long> {
}
