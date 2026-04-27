package com.ceos23.spring_boot.domain.store.repository;

import com.ceos23.spring_boot.domain.store.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
}
