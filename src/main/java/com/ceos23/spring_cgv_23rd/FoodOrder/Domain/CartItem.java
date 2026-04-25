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
public class CartItem {
    protected CartItem(TheaterMenu menu, int quantity){
        this.menu = menu;
        this.quantity = quantity;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Setter
    @ManyToOne
    @JoinColumn(name = "cart_id")
    private Cart cart;

    @ManyToOne
    @JoinColumn(name = "menu_id")
    private TheaterMenu menu;

    private int quantity;

    public int getPrice(){
        return menu.getPrice() * quantity;
    }

    public void addOrder(Cart cart){
        this.cart = cart;
        cart.getCartItems().add(this);
    }

    public OrderItem buy(){
        menu.buy(quantity);
        return new OrderItem(this);
    }
}
