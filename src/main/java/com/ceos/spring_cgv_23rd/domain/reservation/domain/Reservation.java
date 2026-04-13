package com.ceos.spring_cgv_23rd.domain.reservation.domain;

import com.ceos.spring_cgv_23rd.domain.reservation.exception.ReservationErrorCode;
import com.ceos.spring_cgv_23rd.global.apiPayload.exception.GeneralException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
public class Reservation {

    private Long id;
    private Long userId;
    private Long guestId;
    private Long screeningId;
    private String reservationNumber;
    private ReservationStatus status;
    private Integer totalPrice;
    @Builder.Default
    private List<Long> seatIds = new ArrayList<>();
    private LocalDateTime createdAt;


    public static Reservation createReservation(Long userId, Long screeningId, int screeningPrice, List<Long> seatIds) {
        return Reservation.builder()
                .userId(userId)
                .screeningId(screeningId)
                .reservationNumber(generateReservationNumber())
                .status(ReservationStatus.COMPLETED)
                .totalPrice(screeningPrice * seatIds.size())
                .seatIds(seatIds)
                .build();

    }

    public static Reservation createGuestReservation(Long guestId, Long screeningId, int screeningPrice, List<Long> seatIds) {
        return Reservation.builder()
                .guestId(guestId)
                .screeningId(screeningId)
                .reservationNumber(generateReservationNumber())
                .status(ReservationStatus.COMPLETED)
                .totalPrice(screeningPrice * seatIds.size())
                .seatIds(seatIds)
                .build();
    }

    private static String generateReservationNumber() {
        String datePart = LocalDate.now().format(DateTimeFormatter.ofPattern("yyMMdd"));
        String randomPart = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        return datePart + "-" + randomPart;
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
