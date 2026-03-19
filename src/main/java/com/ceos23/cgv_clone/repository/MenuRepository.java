package com.ceos23.cgv_clone.repository;

import com.ceos23.cgv_clone.domain.store.Menu;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MenuRepository extends JpaRepository<Menu, Long> {
}
