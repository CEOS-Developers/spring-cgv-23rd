package com.ceos23.spring_boot.domain.store.entity;

import com.ceos23.spring_boot.domain.theater.entity.Theater;
import com.ceos23.spring_boot.domain.user.entity.User;
import com.ceos23.spring_boot.global.common.BaseSoftDeleteEntity;
import com.ceos23.spring_boot.global.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "orders")
public class Order extends BaseTimeEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "orders_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "theater_id", nullable = false)
    private Theater theater;

    @Column(name = "total_price", nullable = false)
    private Integer totalPrice;

    @Column(nullable = false)
    private Boolean refundable = false;

    @Builder
    public Order(User user, Theater theater, Integer totalPrice, Boolean refundable) {
        this.user = user;
        this.theater = theater;
        this.totalPrice = totalPrice;
        this.refundable = refundable;
    }
}