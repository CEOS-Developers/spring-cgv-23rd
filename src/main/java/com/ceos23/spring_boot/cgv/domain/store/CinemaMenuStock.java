package com.ceos23.spring_boot.cgv.domain.store;

import com.ceos23.spring_boot.cgv.domain.cinema.Cinema;
import com.ceos23.spring_boot.cgv.global.exception.BadRequestException;
import com.ceos23.spring_boot.cgv.global.exception.ErrorCode;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_cinema_menu_stock",
                        columnNames = {"cinema_id", "store_menu_id"}
                )
        }
)
public class CinemaMenuStock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Integer stockQuantity;

    @ManyToOne
    @JoinColumn(name = "cinema_id", nullable = false)
    private Cinema cinema;

    @ManyToOne
    @JoinColumn(name = "store_menu_id", nullable = false)
    private StoreMenu storeMenu;

    public CinemaMenuStock(Integer stockQuantity, Cinema cinema, StoreMenu storeMenu) {
        this.stockQuantity = stockQuantity;
        this.cinema = cinema;
        this.storeMenu = storeMenu;
    }

    public void decreaseStock(Integer quantity) {
        if (quantity == null || quantity < 1) {
            throw new BadRequestException(ErrorCode.BAD_REQUEST);
        }

        if (stockQuantity < quantity) {
            throw new BadRequestException(ErrorCode.INSUFFICIENT_MENU_STOCK);
        }

        this.stockQuantity -= quantity;
    }
}
