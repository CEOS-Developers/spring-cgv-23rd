package com.ceos.spring_cgv_23rd.domain.reservation.adapter.in.web.mapper;

import com.ceos.spring_cgv_23rd.domain.payment.application.dto.result.PaymentResult;
import com.ceos.spring_cgv_23rd.domain.reservation.adapter.in.web.dto.response.ReservationResponse;
import com.ceos.spring_cgv_23rd.domain.reservation.application.dto.result.ReservationDetailResult;
import com.ceos.spring_cgv_23rd.domain.reservation.application.dto.result.ReservationResult;
import org.springframework.stereotype.Component;

@Component
public class ReservationResponseMapper {

    public ReservationResponse.ReservationDetailResponse toResponse(ReservationDetailResult result) {
        return ReservationResponse.ReservationDetailResponse.builder()
                .reservationId(result.reservationId())
                .reservationNumber(result.reservationNumber())
                .status(result.status())
                .movieTitle(result.movieTitle())
                .theaterName(result.theaterName())
                .hallName(result.hallName())
                .startAt(result.startAt())
                .endAt(result.endAt())
                .seats(result.seats().stream()
                        .map(s ->
                                ReservationResponse.SeatInfo.builder()
                                        .seatId(s.seatId())
                                        .rowNum(s.rowNum())
                                        .colNum(s.colNum())
                                        .build())
                        .toList())
                .totalPrice(result.totalPrice())
                .createdAt(result.createdAt())
                .payment(toPaymentInfo(result.payment()))
                .build();
    }

    public ReservationResponse.CreateReservationResponse toResponse(ReservationResult result) {
        return ReservationResponse.CreateReservationResponse.builder()
                .reservationToken(result.reservationToken())
                .screeningId(result.screeningId())
                .seatIds(result.seatIds())
                .expiresAt(result.expiresAt())
                .build();
    }

    private ReservationResponse.PaymentInfo toPaymentInfo(PaymentResult payment) {
        if (payment == null) return null;
        return ReservationResponse.PaymentInfo.builder()
                .paymentId(payment.paymentId())
                .status(payment.status().name())
                .amount(payment.amount())
                .orderName(payment.orderName())
                .pgProvider(payment.pgProvider())
                .paidAt(payment.paidAt())
                .build();
    }
}
