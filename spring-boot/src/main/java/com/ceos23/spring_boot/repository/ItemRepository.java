package com.ceos23.spring_boot.repository;

import com.ceos23.spring_boot.domain.Item;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemRepository extends JpaRepository<Item, Long> {
}