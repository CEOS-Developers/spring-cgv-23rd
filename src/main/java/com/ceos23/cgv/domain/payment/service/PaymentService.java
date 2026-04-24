package com.ceos23.cgv.domain.payment.service;

import com.ceos23.cgv.domain.concession.entity.FoodOrder;
import com.ceos23.cgv.domain.payment.client.PaymentClient;
import com.ceos23.cgv.domain.payment.config.PaymentProperties;
import com.ceos23.cgv.domain.payment.dto.PaymentInstantRequest;
import com.ceos23.cgv.domain.payment.dto.PaymentResponse;
import com.ceos23.cgv.domain.reservation.entity.Reservation;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private static final String CURRENCY_KRW = "KRW";
    private static final String RESERVATION_ORDER_NAME = "CGV 영화 예매";
    private static final String FOOD_ORDER_NAME = "CGV 매점 주문";

    private final PaymentClient paymentClient;
    private final PaymentProperties paymentProperties;

    public String createPaymentId() {
        return "reservation-" + UUID.randomUUID();
    }

    public String createFoodOrderPaymentId() {
        return "food-order-" + UUID.randomUUID();
    }

    public PaymentResponse requestInstantPayment(Reservation reservation) {
        PaymentInstantRequest request = createInstantRequest(
                RESERVATION_ORDER_NAME,
                reservation.getPrice(),
                createCustomData(reservation)
        );

        return paymentClient.requestInstantPayment(reservation.getPaymentId(), request);
    }

    public PaymentResponse requestInstantPayment(FoodOrder foodOrder) {
        PaymentInstantRequest request = createInstantRequest(
                FOOD_ORDER_NAME,
                foodOrder.getTotalPrice(),
                createCustomData(foodOrder)
        );

        return paymentClient.requestInstantPayment(foodOrder.getPaymentId(), request);
    }

    public PaymentResponse cancelPayment(String paymentId) {
        return paymentClient.cancelPayment(paymentId);
    }

    private PaymentInstantRequest createInstantRequest(String orderName, int totalPayAmount, String customData) {
        return new PaymentInstantRequest(
                paymentProperties.getStoreId(),
                orderName,
                totalPayAmount,
                CURRENCY_KRW,
                customData
        );
    }

    private String createCustomData(Reservation reservation) {
        return String.format(
                "{\"reservationId\":%d,\"screeningId\":%d,\"saleNumber\":\"%s\"}",
                reservation.getId(),
                reservation.getScreening().getId(),
                reservation.getSaleNumber()
        );
    }

    private String createCustomData(FoodOrder foodOrder) {
        return String.format(
                "{\"foodOrderId\":%d,\"cinemaId\":%d}",
                foodOrder.getId(),
                foodOrder.getCinema().getId()
        );
    }
}
