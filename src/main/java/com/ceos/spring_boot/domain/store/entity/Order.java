package com.ceos.spring_boot.domain.store.entity;

import com.ceos.spring_boot.domain.cinema.entity.Cinema;
import com.ceos.spring_boot.domain.user.entity.User;
import com.ceos.spring_boot.global.codes.ErrorCode;
import com.ceos.spring_boot.global.entity.BaseEntity;
import com.ceos.spring_boot.global.exception.BusinessException;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@AllArgsConstructor
@Table(name = "orders")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Order extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cinema_id")
    private Cinema cinema;

    private Integer totalPrice; // 총 가격

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;

    @Builder.Default
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> orderItems = new ArrayList<>();

    public static Order create(User user, Cinema cinema) {
        return Order.builder()
                .user(user)
                .cinema(cinema)
                .status(OrderStatus.PENDING)
                .totalPrice(0)
                .orderItems(new ArrayList<>())
                .build();
    }

    public void complete() {
        if (this.status != OrderStatus.PENDING) {
            throw new BusinessException(ErrorCode.INVALID_PAYMENT_STATUS);
        }
        this.status = OrderStatus.COMPLETED;
    }

    public void cancel() {
        if (this.status == OrderStatus.CANCELLED) {
            throw new BusinessException(ErrorCode.ALREADY_CANCELLED_PAYMENT);
        }
        this.status = OrderStatus.CANCELLED;
    }

    public void addOrderItem(OrderItem orderItem) {
        this.orderItems.add(orderItem);
        if (orderItem.getOrder() != this) {
            orderItem.setOrder(this);
        }
    }

    public void updateTotalPrice(Integer price) {
        this.totalPrice = price;
    }

}
