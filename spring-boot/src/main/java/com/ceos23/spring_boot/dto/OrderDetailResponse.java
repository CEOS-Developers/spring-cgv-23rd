package com.ceos23.spring_boot.dto;

import com.ceos23.spring_boot.domain.OrderDetail;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class OrderDetailResponse {

    private Long itemId;
    private String itemName;
    private Integer price;
    private Integer count;

    public static OrderDetailResponse from(OrderDetail orderDetail) {
        return OrderDetailResponse.builder()
                .itemId(orderDetail.getItem().getId())
                .itemName(orderDetail.getItem().getName())
                .price(orderDetail.getItem().getPrice())
                .count(orderDetail.getCount())
                .build();
    }
}