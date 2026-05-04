package com.ceos23.spring_cgv_23rd.FoodOrder.Domain;

import com.ceos23.spring_cgv_23rd.Theater.Domain.TheaterMenu;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderItem {
    public OrderItem(CartItem ci){
        this(ci.getMenu(), ci.getQuantity());
    }

    protected OrderItem(TheaterMenu menu, int quantity){
        this.menu = menu;
        this.quantity = quantity;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Setter
    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;

    @ManyToOne
    @JoinColumn(name = "menu_id")
    private TheaterMenu menu;

    private int quantity;

    public int getPrice(){
        return menu.getPrice() * quantity;
    }
}
