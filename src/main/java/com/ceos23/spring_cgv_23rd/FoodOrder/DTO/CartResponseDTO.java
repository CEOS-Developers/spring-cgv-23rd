package com.ceos23.spring_cgv_23rd.FoodOrder.DTO;

import com.ceos23.spring_cgv_23rd.FoodOrder.Domain.Cart;
import com.ceos23.spring_cgv_23rd.FoodOrder.Domain.Order;
import com.ceos23.spring_cgv_23rd.Theater.Domain.Theater;

import java.util.ArrayList;
import java.util.List;

public record CartResponseDTO(
        long theaterId,
        String theaterName,

        long orderId,
        long userId,
        List<CartItemWrapperDTO> items,
        long price
) {
    public static CartResponseDTO create(Cart cart){
        Theater theater = cart.getTheater();

        return new CartResponseDTO(
                theater.getId(),
                theater.getName(),

                cart.getId(),
                cart.getUser().getId(),
                CartItemWrapperDTO.create(cart.getCartItems()),
                cart.getPrice());
    }
}