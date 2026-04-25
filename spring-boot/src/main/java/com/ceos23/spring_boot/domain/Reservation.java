package com.ceos23.spring_boot.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_reservation_screening_seat",
                        columnNames = {"screening_id", "seat_id"}
                )
        }
)
public class Reservation {

    private static final int PAYMENT_EXPIRE_MINUTES = 5;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Screening screening;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Seat seat;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReservationStatus reservationStatus;

    private String paymentId;

    private LocalDateTime reservedAt;

    private LocalDateTime paidAt;

    private LocalDateTime expiresAt;

    private Reservation(User user, Screening screening, Seat seat) {
        this.user = user;
        this.screening = screening;
        this.seat = seat;
        this.reservationStatus = ReservationStatus.PENDING_PAYMENT;
        this.expiresAt = LocalDateTime.now().plusMinutes(PAYMENT_EXPIRE_MINUTES);
    }

    public static Reservation create(User user, Screening screening, Seat seat) {
        return new Reservation(user, screening, seat);
    }

    @PrePersist
    protected void onCreate() {
        this.reservedAt = LocalDateTime.now();
    }

    public void markPaid(String paymentId, LocalDateTime paidAt) {
        this.paymentId = paymentId;
        this.paidAt = paidAt;
        this.reservationStatus = ReservationStatus.PAID;
    }

    public boolean isExpired(LocalDateTime now) {
        return this.reservationStatus == ReservationStatus.PENDING_PAYMENT
                && this.expiresAt.isBefore(now);
    }

    public boolean isPaid() {
        return this.reservationStatus == ReservationStatus.PAID;
    }
}