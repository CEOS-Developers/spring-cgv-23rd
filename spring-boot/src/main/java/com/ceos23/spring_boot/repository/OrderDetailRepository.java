package com.ceos23.spring_boot.repository;

import com.ceos23.spring_boot.domain.OrderDetail;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderDetailRepository extends JpaRepository<OrderDetail, Long> {
}