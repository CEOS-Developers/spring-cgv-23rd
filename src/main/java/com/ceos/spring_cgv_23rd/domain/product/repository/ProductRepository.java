package com.ceos.spring_cgv_23rd.domain.product.repository;

import com.ceos.spring_cgv_23rd.domain.product.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findAllByIdIn(List<Long> productIds);
}
