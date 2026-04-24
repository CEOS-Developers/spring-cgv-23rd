package com.ceos23.cgv.domain.concession.entity;

import com.ceos23.cgv.domain.cinema.entity.Cinema;
import com.ceos23.cgv.domain.user.entity.User;
import com.ceos23.cgv.global.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "food_orders")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class FoodOrder extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cinema_id", nullable = false)
    private Cinema cinema;

    @Column(nullable = false)
    private int totalPrice;

    public static FoodOrder create(User user, Cinema cinema) {
        return FoodOrder.builder()
                .user(user)
                .cinema(cinema)
                .totalPrice(0)
                .build();
    }

    // 총 결제 금액을 업데이트하는 메서드
    public void updateTotalPrice(int totalPrice) {
        this.totalPrice = totalPrice;
    }
}
