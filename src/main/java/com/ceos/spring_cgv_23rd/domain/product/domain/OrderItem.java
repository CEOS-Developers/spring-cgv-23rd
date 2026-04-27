package com.ceos.spring_cgv_23rd.domain.product.domain;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class OrderItem {

    private Long id;
    private Long productId;
    private Integer quantity;
    private Integer price;


    public static OrderItem createOrderItem(Long productId, Integer price, int quantity) {
        return OrderItem.builder()
                .productId(productId)
                .quantity(quantity)
                .price(price)
                .build();
    }

}
