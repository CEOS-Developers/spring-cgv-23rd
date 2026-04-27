package com.ceos23.spring_cgv_23rd.Food.DTO;

import com.ceos23.spring_cgv_23rd.Food.Domain.Food;
import com.ceos23.spring_cgv_23rd.Food.Domain.MenuType;
import jakarta.persistence.*;

public record FoodWrapperDTO (
        long id,
        String menuName,
        int price,
        MenuType menuType
){
    public static FoodWrapperDTO create(Food food){
        return new FoodWrapperDTO(
                food.getId(),
                food.getFoodName(),
                food.getPrice(),
                food.getMenuType()
        );
    }

}