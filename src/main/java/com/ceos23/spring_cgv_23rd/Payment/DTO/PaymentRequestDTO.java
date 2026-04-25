package com.ceos23.spring_cgv_23rd.Payment.DTO;

import com.ceos23.spring_cgv_23rd.FoodOrder.Domain.Cart;
import com.ceos23.spring_cgv_23rd.FoodOrder.Domain.Order;
import com.ceos23.spring_cgv_23rd.Reservation.Domain.Reservation;
import lombok.Getter;

public record PaymentRequestDTO(
        String storeId,
        String orderName,
        int totalPayAmount,
        Currency currency,
        String customData
) {
    public static PaymentRequestDTO create(Reservation reservation,
                                           String storeId, String orderName, String customData){
        return new PaymentRequestDTO(
                storeId, orderName, reservation.getTotalPrice(), Currency.KRW, customData
        );
    }

    public static PaymentRequestDTO create(Reservation reservation,
                                           String storeId, String orderName){
        return new PaymentRequestDTO(
                storeId, orderName, reservation.getTotalPrice(), Currency.KRW, ""
        );
    }

    public static PaymentRequestDTO create(Cart cart,
                                           String storeId, String orderName, String customData){
        return new PaymentRequestDTO(
                storeId, orderName, cart.getPrice(), Currency.KRW, customData
        );
    }

    public static PaymentRequestDTO create(Cart cart,
                                           String storeId, String orderName){
        return new PaymentRequestDTO(
                storeId, orderName, cart.getPrice(), Currency.KRW, ""
        );
    }
}
