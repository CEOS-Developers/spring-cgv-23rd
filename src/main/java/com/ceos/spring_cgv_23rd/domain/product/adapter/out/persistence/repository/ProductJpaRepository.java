package com.ceos.spring_cgv_23rd.domain.product.adapter.out.persistence.repository;

import com.ceos.spring_cgv_23rd.domain.product.adapter.out.persistence.entity.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductJpaRepository extends JpaRepository<ProductEntity, Long> {

    List<ProductEntity> findAllByIdIn(List<Long> productIds);
}
