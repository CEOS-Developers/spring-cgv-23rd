package com.ceos23.cgv_clone.store.repository;

import com.ceos23.cgv_clone.store.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
