package com.ceos23.cgv.domain.concession.dto;

import com.ceos23.cgv.domain.concession.entity.FoodOrder;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FoodOrderResponse {
    private Long orderId;
    private String userName;
    private String cinemaName;
    private int totalPrice;

    public static FoodOrderResponse from(FoodOrder foodOrder) {
        return FoodOrderResponse.builder()
                .orderId(foodOrder.getId())
                .userName(foodOrder.getUser().getNickname())
                .cinemaName(foodOrder.getCinema().getName())
                .totalPrice(foodOrder.getTotalPrice())
                .build();
    }
}