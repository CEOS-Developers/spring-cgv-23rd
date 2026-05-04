package com.ceos23.spring_cgv_23rd.FoodOrder.Domain;

import com.ceos23.spring_cgv_23rd.Reservation.Domain.ReservationStatus;
import com.ceos23.spring_cgv_23rd.Theater.Domain.Theater;
import com.ceos23.spring_cgv_23rd.Theater.Domain.TheaterMenu;
import com.ceos23.spring_cgv_23rd.User.Domain.User;
import com.ceos23.spring_cgv_23rd.global.Exception.CustomException;
import com.ceos23.spring_cgv_23rd.global.Exception.ErrorCode;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Entity
@Table(name = "cart",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "USER_UNIQUE",
                        columnNames = {"user_id", "theater_id", "status", "activate_key"}
                )
        }
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Cart {
    private Cart(User user, Theater theater){
        this.user = user;
        this.theater = theater;
        this.status = ReservationStatus.RESERVED;
        this.activateKey = "RESERVED";
    }

    @Id
    @Getter
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Getter
    private ReservationStatus status;

    private String activateKey;

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

    private void changeActivateKey() {
        if(activateKey.equals("RESERVED")){
            this.activateKey = UUID.randomUUID().toString();
        }
    }

    public void addItem(TheaterMenu menu, int quantity){
        if (quantity < 0){
            throw new CustomException(ErrorCode.INVALID_QUANTITY);
        }

        if (menu.getSold() < quantity){
            throw new CustomException(ErrorCode.INVENTORY_SHORTAGE);
        }

        CartItem ci = new CartItem(menu, quantity);
        cartItems.add(ci);
        ci.setCart(this);

        price += ci.getPrice();
    }

    public void endPaying(){
        this.status = ReservationStatus.PAID;
        changeActivateKey();
    }

    public void cancel(){
        this.status = ReservationStatus.CANCELED;
        changeActivateKey();
    }

    public Optional<CartItem> checkingUnavailableCartItem(){
        for (CartItem cartItem : cartItems){
            if (cartItem.isQuantityExceedSold()){
                return Optional.of(cartItem);
            }
        }

        return Optional.empty();
    }

    public static Cart create(User user, Theater theater){
        return new Cart(user, theater);
    }
}
