package com.ceos23.spring_cgv_23rd.FoodOrder.DTO;

import com.ceos23.spring_cgv_23rd.FoodOrder.Domain.Cart;
import com.ceos23.spring_cgv_23rd.Theater.Domain.Theater;

import java.util.List;

public record CartResponseDTO(
        long theaterId,
        String theaterName,

        long cartId,
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

    public static CartResponseDTO empty(){
        return new CartResponseDTO(
                0L,
                "",
                0L,
                0L,
                List.of(),
                0
        );
    }
}