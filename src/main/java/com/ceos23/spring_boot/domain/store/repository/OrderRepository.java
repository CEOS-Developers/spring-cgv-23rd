package com.ceos23.spring_boot.domain.store.repository;

import com.ceos23.spring_boot.domain.store.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
