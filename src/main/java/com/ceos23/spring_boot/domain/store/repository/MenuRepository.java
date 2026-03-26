package com.ceos23.spring_boot.domain.store.repository;

import com.ceos23.spring_boot.domain.store.entity.Menu;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MenuRepository extends JpaRepository<Menu, Long> {
}
