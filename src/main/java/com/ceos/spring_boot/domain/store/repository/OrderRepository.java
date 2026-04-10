package com.ceos.spring_boot.domain.store.repository;

import com.ceos.spring_boot.domain.store.entity.Order;
import com.ceos.spring_boot.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {

    // 특정 사용자의 주문 내역을 최신순으로 조회
    List<Order> findByUserOrderByCreatedAtDesc(User user);

}
