package com.ceos23.spring_boot.repository;

import com.ceos23.spring_boot.domain.ItemOrder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ItemOrderRepository extends JpaRepository<ItemOrder, Long> {

    List<ItemOrder> findAllByUserId(Long userId);
}