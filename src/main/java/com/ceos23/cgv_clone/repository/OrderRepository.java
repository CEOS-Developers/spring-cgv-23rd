package com.ceos23.cgv_clone.repository;

import com.ceos23.cgv_clone.domain.store.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
