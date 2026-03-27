package com.cgv.spring_boot.domain.store.entity;

import com.cgv.spring_boot.domain.store.exception.StoreErrorCode;
import com.cgv.spring_boot.global.common.entity.BaseEntity;
import com.cgv.spring_boot.global.error.exception.BusinessException;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@Table(name = "order_items")
public class OrderItem extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_item_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private StoreOrder order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;

    @Column(name = "order_price", nullable = false)
    private int orderPrice; // 주문 시점 가격

    @Column(name = "count", nullable = false)
    private int count; // 주문 수량

    @Builder
    public OrderItem(StoreOrder order, Item item, int orderPrice, int count) {
        validateCount(count);
        this.order = order;
        this.item = item;
        this.orderPrice = orderPrice;
        this.count = count;
    }

    private void validateCount(int count) {
        if (count < 1) {
            throw new BusinessException(StoreErrorCode.INVALID_STOCK_QUANTITY);
        }
    }

    /**
     * 해당 상품의 총 금액 계산 로직
     */
    public int getTotalPrice() {
        return getOrderPrice() * getCount();
    }
}
