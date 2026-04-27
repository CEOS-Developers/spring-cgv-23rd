package com.ceos.spring_cgv_23rd.domain.product.adapter.out.persistence.repository;

import com.ceos.spring_cgv_23rd.domain.product.adapter.out.persistence.entity.ProductOrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ProductOrderJpaRepository extends JpaRepository<ProductOrderEntity, Long> {

    @Query("SELECT o FROM ProductOrderEntity o " +
            "JOIN FETCH o.orderItems oi " +
            "JOIN FETCH oi.product " +
            "WHERE o.id = :orderId")
    Optional<ProductOrderEntity> findWithOrderItemsById(Long orderId);
}
