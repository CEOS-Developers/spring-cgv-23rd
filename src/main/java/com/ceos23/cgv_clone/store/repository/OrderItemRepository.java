package com.ceos23.cgv_clone.store.repository;

import com.ceos23.cgv_clone.store.domain.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
}
