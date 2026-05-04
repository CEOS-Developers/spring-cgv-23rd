package com.ceos23.cgv_clone.reservation.entity;

import com.ceos23.cgv_clone.global.entity.BaseEntity;
import com.ceos23.cgv_clone.global.exception.CustomException;
import com.ceos23.cgv_clone.global.response.ErrorCode;
import com.ceos23.cgv_clone.theater.entity.Schedule;
import com.ceos23.cgv_clone.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name = "reservations")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Reservation extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reservation_id")
    private Long id;

    @Column(nullable = false)
    private LocalDateTime reservedAt;

    @Column(nullable = false)
    private int totalPrice;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReservationStatus status;

    @Column(nullable = false, unique = true)
    private String paymentId;

    @JoinColumn(name = "user_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @JoinColumn(name = "schedule_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Schedule schedule;

    @OneToMany(mappedBy = "reservation", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReservationSeat> reservationSeats = new ArrayList<>();

    @Builder
    public Reservation(LocalDateTime reservedAt, int totalPrice, ReservationStatus status, String paymentId, User user, Schedule schedule) {
        this.reservedAt = reservedAt;
        this.totalPrice = totalPrice;
        this.status = status;
        this.paymentId = paymentId;
        this.user = user;
        this.schedule = schedule;
    }

    public static Reservation createPending(User user, Schedule schedule, int totalPrice, String paymentId) {
        return Reservation.builder()
                .reservedAt(LocalDateTime.now())
                .totalPrice(totalPrice)
                .status(ReservationStatus.PENDING)
                .paymentId(paymentId)
                .user(user)
                .schedule(schedule)
                .build();
    }

    public void addReservationSeat(ReservationSeat seat) {
        this.reservationSeats.add(seat);
    }

    public void confirm() {
        if (status != ReservationStatus.PENDING) {
            throw new CustomException(ErrorCode.INVALID_RESERVATION_STATUS);
        }

        this.status = ReservationStatus.RESERVED;
    }

    public void cancelPending() {
        if (status != ReservationStatus.PENDING) {
            throw new CustomException(ErrorCode.INVALID_RESERVATION_STATUS);
        }

        this.status = ReservationStatus.CANCELED;
    }

    public void cancelReserved() {
        if (status == ReservationStatus.CANCELED) {
            throw new CustomException(ErrorCode.ALREADY_CANCELED_RESERVATION);
        }

        if (status != ReservationStatus.RESERVED) {
            throw new CustomException(ErrorCode.INVALID_RESERVATION_STATUS);
        }

        this.status = ReservationStatus.CANCELED;
    }

    public void validateCancelable() {
        if (status == ReservationStatus.CANCELED) {
            throw new CustomException(ErrorCode.ALREADY_CANCELED_RESERVATION);
        }

        if (status != ReservationStatus.RESERVED) {
            throw new CustomException(ErrorCode.INVALID_RESERVATION_STATUS);
        }
    }
}
