package com.ceos23.spring_cgv_23rd.Food.Repository;

import com.ceos23.spring_cgv_23rd.Food.Domain.Food;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FoodRepository extends JpaRepository<Food, Long> {
}
