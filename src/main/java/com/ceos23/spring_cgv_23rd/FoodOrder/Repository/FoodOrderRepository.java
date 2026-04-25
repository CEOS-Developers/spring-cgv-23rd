package com.ceos23.spring_cgv_23rd.FoodOrder.Repository;

import com.ceos23.spring_cgv_23rd.FoodOrder.Domain.Order;
import com.ceos23.spring_cgv_23rd.User.Domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FoodOrderRepository extends JpaRepository<Order, Long> {
    Optional<Order> findByUser(User user);

    List<Order> findAllByUser(User user);
}
