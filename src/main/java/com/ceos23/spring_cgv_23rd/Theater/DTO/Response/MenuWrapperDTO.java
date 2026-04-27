package com.ceos23.spring_cgv_23rd.Theater.DTO.Response;

import com.ceos23.spring_cgv_23rd.Food.DTO.FoodWrapperDTO;
import com.ceos23.spring_cgv_23rd.Theater.Domain.TheaterMenu;

import java.util.ArrayList;
import java.util.List;

public record MenuWrapperDTO(
        long id,
        int sold,
        boolean soldOut,
        boolean ablePickUpLater,

        FoodWrapperDTO food
) {
    public static MenuWrapperDTO create(TheaterMenu theaterMenu){
        return new MenuWrapperDTO(
                theaterMenu.getId(),
                theaterMenu.getSold(),
                theaterMenu.isSoldOut(),
                theaterMenu.isAblePickUpLater(),
                FoodWrapperDTO.create(theaterMenu.getFood())
        );
    }

    public static List<MenuWrapperDTO> create(List<TheaterMenu> theaterMenu){
        List<MenuWrapperDTO> res = new ArrayList<>();

        for (TheaterMenu tm : theaterMenu){
            res.add(MenuWrapperDTO.create(tm));
        }

        return res;
    }
}