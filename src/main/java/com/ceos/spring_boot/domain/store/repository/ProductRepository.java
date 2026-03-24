package com.ceos.spring_boot.domain.store.repository;

import com.ceos.spring_boot.domain.store.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {}
