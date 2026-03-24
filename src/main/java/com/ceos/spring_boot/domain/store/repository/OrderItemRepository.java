package com.ceos.spring_boot.domain.store.repository;

import com.ceos.spring_boot.domain.store.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    // 하나의 주문에 속한 모든 상세 상품 목록 조회
    List<OrderItem> findByOrderId(Long orderId);
}