package com.ceos.spring_cgv_23rd.domain.product.repository;

import com.ceos.spring_cgv_23rd.domain.product.entity.ProductOrder;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductOrderRepository extends JpaRepository<ProductOrder, Long> {
}
