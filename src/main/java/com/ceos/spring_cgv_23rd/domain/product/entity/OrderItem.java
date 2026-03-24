package com.ceos.spring_cgv_23rd.domain.product.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "order_item", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"product_order_id", "product_id"})
})
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_item_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_order_id", nullable = false)
    private ProductOrder productOrder;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Column(name = "price", nullable = false)
    private Integer price;


    public static OrderItem createOrderItem(Product product, int quantity) {
        return OrderItem.builder()
                .product(product)
                .quantity(quantity)
                .price(product.getPrice())
                .build();
    }

    public void assignOrder(ProductOrder order) {
        this.productOrder = order;
    }
}
