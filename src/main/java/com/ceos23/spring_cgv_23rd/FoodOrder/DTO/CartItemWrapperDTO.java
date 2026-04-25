package com.ceos23.spring_cgv_23rd.FoodOrder.DTO;

import com.ceos23.spring_cgv_23rd.FoodOrder.Domain.CartItem;
import com.ceos23.spring_cgv_23rd.FoodOrder.Domain.OrderItem;
import com.ceos23.spring_cgv_23rd.Theater.Domain.TheaterMenu;

import java.util.List;

public record CartItemWrapperDTO(
        long id,
        TheaterMenuWrapperDTO menu,
        int quantity
) {
    public static CartItemWrapperDTO create (CartItem ci){
        return new CartItemWrapperDTO(
                ci.getId(), TheaterMenuWrapperDTO.create(ci.getMenu()), ci.getQuantity()
        );
    }

    public static List<CartItemWrapperDTO> create(List<CartItem> cis){
        return cis.stream()
                .map(CartItemWrapperDTO::create)
                .toList();
    }
}