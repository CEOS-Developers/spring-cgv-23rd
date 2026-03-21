package com.ceos.spring_cgv_23rd.domain.product.repository;

import com.ceos.spring_cgv_23rd.domain.product.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    
    List<OrderItem> findByProductOrderId(Long productOrderId);
}
