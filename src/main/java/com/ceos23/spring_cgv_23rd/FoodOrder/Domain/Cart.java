package com.ceos23.spring_cgv_23rd.FoodOrder.Domain;

import com.ceos23.spring_cgv_23rd.Reservation.Domain.ReservationStatus;
import com.ceos23.spring_cgv_23rd.Theater.Domain.Theater;
import com.ceos23.spring_cgv_23rd.Theater.Domain.TheaterMenu;
import com.ceos23.spring_cgv_23rd.User.Domain.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "cart",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "USER_UNIQUE",
                        columnNames = {"user_id", "active_key"}
                )
        }
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Cart {
    private Cart(User user, Theater theater){
        this.user = user;
        this.theater = theater;
        this.status = ReservationStatus.RESERVED;
        this.activeKey = "RESERVED";
    }

    @Id
    @Getter
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Getter
    private ReservationStatus status;

    private String activeKey;

    @Getter
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Getter
    @ManyToOne
    @JoinColumn(name = "theater_id")
    private Theater theater;

    @Getter
    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL)
    private List<CartItem> cartItems = new ArrayList<>();

    @Getter
    private int price = 0;

    public void addItem(TheaterMenu menu, int quantity){
        if (menu.getSold() < quantity){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "재고가 없습니다.");
        }

        CartItem ci = new CartItem(menu, quantity);
        cartItems.add(ci);
        ci.setCart(this);

        price += ci.getPrice();
    }

    public void startBuying(){
        this.status = ReservationStatus.PENDING;
    }

    public void endPaying(){
        this.status = ReservationStatus.PAID;

        if (activeKey.equals("RESERVED")){
            this.activeKey = UUID.randomUUID().toString();
        }
    }

    public void cancel(){
        this.status = ReservationStatus.CANCELED;

        if (activeKey.equals("RESERVED")){
            this.activeKey = UUID.randomUUID().toString();
        }

    }

    public Order buyCart(){
        if (this.status != ReservationStatus.PENDING){
            throw new IllegalStateException("결제 중 상태에서만 주문 가능");
        }

        if (!activeKey.equals("RESERVED")){
            this.activeKey = UUID.randomUUID().toString();
        }

        Order order = Order.create(this);

        for (CartItem ci : cartItems){
            OrderItem oi = ci.buy();
            order.addOrderItem(oi);
        }

        endPaying();
        return order;
    }

    public boolean isAvailable(){
        return this.status == ReservationStatus.RESERVED;
    }

    public static Cart create(User user, Theater theater){
        return new Cart(user, theater);
    }
}
