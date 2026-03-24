package com.ceos23.spring_boot.cgv.repository.store;

import com.ceos23.spring_boot.cgv.domain.store.StoreMenu;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StoreMenuRepository extends JpaRepository<StoreMenu, Long> {
}