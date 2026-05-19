package com.ceos23.spring_boot.repository;

import com.ceos23.spring_boot.domain.ItemOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ItemOrderRepository extends JpaRepository<ItemOrder, Long> {

    List<ItemOrder> findAllByUserId(Long userId);

    @Query("""
            select distinct io
            from ItemOrder io
            join fetch io.user
            join fetch io.theater
            left join fetch io.orderDetails od
            left join fetch od.item
            where io.id = :orderId
            """)
    Optional<ItemOrder> findWithDetailsById(@Param("orderId") Long orderId);

    @Query("""
            select distinct io
            from ItemOrder io
            join fetch io.user
            join fetch io.theater
            left join fetch io.orderDetails od
            left join fetch od.item
            where io.user.id = :userId
            order by io.orderedAt desc
            """)
    List<ItemOrder> findAllWithDetailsByUserId(@Param("userId") Long userId);
}