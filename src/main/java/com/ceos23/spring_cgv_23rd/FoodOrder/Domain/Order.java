package com.ceos23.spring_cgv_23rd.FoodOrder.Domain;

import com.ceos23.spring_cgv_23rd.Reservation.Domain.ReservationStatus;
import com.ceos23.spring_cgv_23rd.Theater.Domain.Theater;
import com.ceos23.spring_cgv_23rd.User.Domain.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Table(name = "orders")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Order {
    private Order(User user, Theater theater, int price, ReservationStatus status){
        this.user = user;
        this.theater = theater;
        this.status = status;
        this.price = price;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private ReservationStatus status;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "theater_id")
    private Theater theater;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderItem> orderItems = new ArrayList<>();

    private int price;

    public void addOrderItem(OrderItem oi){
        orderItems.add(oi);
        oi.setOrder(this);
    }

    public static Order create(Cart cart){
        return new Order(cart.getUser(), cart.getTheater(), cart.getPrice(), cart.getStatus());
    }
}
