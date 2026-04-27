package com.ceos23.spring_cgv_23rd.FoodOrder.DTO;

import com.ceos23.spring_cgv_23rd.Theater.Domain.TheaterMenu;

public record TheaterMenuWrapperDTO(
        long menuId,
        String foodName,
        long price
) {
    public static TheaterMenuWrapperDTO create(TheaterMenu tm){
        return new TheaterMenuWrapperDTO(
                tm.getId(), tm.getFood().getFoodName(), tm.getPrice()
        );
    }
}
