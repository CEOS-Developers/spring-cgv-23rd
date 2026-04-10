package com.ceos23.spring_boot.cgv.domain.store;

import com.ceos23.spring_boot.cgv.domain.user.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "store_purchase")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StorePurchase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false)
    private Integer totalPrice;

    @Column(name = "purchased_at", nullable = false)
    private LocalDateTime purchasedAt;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "cinema_menu_stock_id", nullable = false)
    private CinemaMenuStock cinemaMenuStock;

    public StorePurchase(Integer quantity, User user, CinemaMenuStock cinemaMenuStock) {
        this.quantity = quantity;
        this.user = user;
        this.cinemaMenuStock = cinemaMenuStock;
        this.totalPrice = cinemaMenuStock.getStoreMenu().getPrice() * quantity;
        this.purchasedAt = LocalDateTime.now();
    }
}