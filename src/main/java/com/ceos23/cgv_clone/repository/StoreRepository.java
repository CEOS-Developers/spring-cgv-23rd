package com.ceos23.cgv_clone.repository;

import com.ceos23.cgv_clone.domain.store.Store;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StoreRepository extends JpaRepository<Store, Long> {
}
