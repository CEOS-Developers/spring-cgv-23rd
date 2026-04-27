package com.ceos23.spring_cgv_23rd.FoodOrder.DTO;

import com.ceos23.spring_cgv_23rd.FoodOrder.Domain.Cart;
import com.ceos23.spring_cgv_23rd.FoodOrder.Domain.Order;
import com.ceos23.spring_cgv_23rd.Theater.Domain.Theater;

import java.util.ArrayList;
import java.util.List;

public record OrderResponseDTO(
        long theaterId,
        String theaterName,

        long orderId,
        long userId,
        List<OrderItemWrapperDTO> items,
        long price
) {
    public static List<OrderResponseDTO> create(List<Order> orders){
        List<OrderResponseDTO> res = new ArrayList<>();

        for (Order o : orders){
            res.add(create(o));
        }

        return res;
    }

    public static OrderResponseDTO create(Order order){
        Theater theater = order.getTheater();

        return new OrderResponseDTO(
                theater.getId(),
                theater.getName(),

                order.getId(),
                order.getUser().getId(),
                OrderItemWrapperDTO.create(order.getOrderItems()),
                order.getPrice());
    }
}