package com.ceos23.spring_cgv_23rd.FoodOrder.DTO;

import com.ceos23.spring_cgv_23rd.FoodOrder.Domain.OrderItem;
import com.ceos23.spring_cgv_23rd.Theater.Domain.TheaterMenu;

import java.util.List;

public record OrderItemWrapperDTO(
        long id,
        TheaterMenuWrapperDTO menu,
        int quantity
) {
    public static OrderItemWrapperDTO create (OrderItem oi){
        return new OrderItemWrapperDTO(
                oi.getId(), TheaterMenuWrapperDTO.create(oi.getMenu()), oi.getQuantity()
        );
    }

    public static List<OrderItemWrapperDTO> create(List<OrderItem> ois){
        return ois.stream()
                .map(OrderItemWrapperDTO::create)
                .toList();
    }
}

//@Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private long id;
//
//    @Setter
//    @ManyToOne
//    @JoinColumn(name = "order_id")
//    private Order order;
//
//    @ManyToOne
//    @JoinColumn(name = "menu_id")
//    private TheaterMenu menu;
//
//    private int quantity;