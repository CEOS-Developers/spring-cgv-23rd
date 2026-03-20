package com.ceos23.cgv.domain.concession.repository;

import com.ceos23.cgv.domain.concession.entity.FoodOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface FoodOrderRepository extends JpaRepository<FoodOrder, Long> {
    // 특정 유저의 매점 주문 내역을 모두 가져오는 메서드
    List<FoodOrder> findByUserId(Long userId);
}