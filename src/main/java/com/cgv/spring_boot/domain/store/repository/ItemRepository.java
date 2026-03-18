package com.cgv.spring_boot.domain.store.repository;

import com.cgv.spring_boot.domain.store.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {
}
