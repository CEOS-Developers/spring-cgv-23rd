package com.ceos.spring_cgv_23rd.domain.reservation.domain;

import com.ceos.spring_cgv_23rd.domain.reservation.exception.ReservationErrorCode;
import com.ceos.spring_cgv_23rd.global.apiPayload.exception.GeneralException;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
public class Reservation {

    private final Long id;
    private final Long userId;
    private final Long guestId;
    private final Long screeningId;
    private final String reservationNumber;
    private final String paymentId;
    private final Integer totalPrice;
    private final List<Long> seatIds;
    private final LocalDateTime createdAt;
    private ReservationStatus status;


    @Builder(access = AccessLevel.PRIVATE)
    private Reservation(Long id, Long userId, Long guestId, Long screeningId,
                        String reservationNumber, String paymentId,
                        ReservationStatus status, Integer totalPrice,
                        List<Long> seatIds, LocalDateTime createdAt) {
        this.id = id;
        this.userId = userId;
        this.guestId = guestId;
        this.screeningId = screeningId;
        this.reservationNumber = reservationNumber;
        this.paymentId = paymentId;
        this.status = status;
        this.totalPrice = totalPrice;
        this.seatIds = seatIds != null ? seatIds : new ArrayList<>();
        this.createdAt = createdAt;
    }


    public static Reservation createReservation(Long userId, Long screeningId, int screeningPrice,
                                                String paymentId, List<Long> seatIds) {
        return Reservation.builder()
                .userId(userId)
                .screeningId(screeningId)
                .reservationNumber(generateReservationNumber())
                .paymentId(paymentId)
                .status(ReservationStatus.COMPLETED)
                .totalPrice(screeningPrice * seatIds.size())
                .seatIds(seatIds)
                .build();

    }

    public static Reservation createGuestReservation(Long guestId, Long screeningId, int screeningPrice,
                                                     String paymentId, List<Long> seatIds) {
        return Reservation.builder()
                .guestId(guestId)
                .screeningId(screeningId)
                .reservationNumber(generateReservationNumber())
                .paymentId(paymentId)
                .status(ReservationStatus.COMPLETED)
                .totalPrice(screeningPrice * seatIds.size())
                .seatIds(seatIds)
                .build();
    }

    public static Reservation restore(Long id, Long userId, Long guestId, Long screeningId,
                                      String reservationNumber, String paymentId,
                                      ReservationStatus status,
                                      Integer totalPrice, List<Long> seatIds, LocalDateTime createdAt) {
        return Reservation.builder()
                .id(id)
                .userId(userId)
                .guestId(guestId)
                .screeningId(screeningId)
                .reservationNumber(reservationNumber)
                .paymentId(paymentId)
                .status(status)
                .totalPrice(totalPrice)
                .seatIds(seatIds)
                .createdAt(createdAt)
                .build();
    }

    private static String generateReservationNumber() {
        String datePart = LocalDate.now().format(DateTimeFormatter.ofPattern("yyMMdd"));
        String randomPart = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        return datePart + "-" + randomPart;
    }

    public static int calculateTotalPrice(int screeningPrice, int seatCount) {
        return screeningPrice * seatCount;
    }

    public static String generateOrderName(String title, int seatCount) {
        return title + " " + seatCount + "석";
    }

    public void cancel() {
        if (this.status == ReservationStatus.CANCELLED) {
            throw new GeneralException(ReservationErrorCode.ALREADY_CANCELLED);
        }
        this.status = ReservationStatus.CANCELLED;
    }

    public int getSeatCount() {
        return seatIds.size();
    }

    public boolean isGuest() {
        return userId == null && guestId != null;
    }
}
