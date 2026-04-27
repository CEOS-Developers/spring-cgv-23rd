package com.ceos23.spring_boot.cgv.domain.reservation;

import com.ceos23.spring_boot.cgv.domain.movie.Screening;
import com.ceos23.spring_boot.cgv.domain.user.User;
import com.ceos23.spring_boot.cgv.global.exception.ConflictException;
import com.ceos23.spring_boot.cgv.global.exception.ErrorCode;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ReservationStatus status;

    @Column(nullable = false)
    private LocalDateTime reservedAt;

    @Column
    private LocalDateTime expiresAt;

    @Column(name = "payment_id", nullable = false, unique = true, updatable = false, length = 100)
    private String paymentId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "screening_id", nullable = false)
    private Screening screening;

    public Reservation(User user, Screening screening, String paymentId, LocalDateTime expiresAt) {
        this.user = user;
        this.screening = screening;
        this.paymentId = paymentId;
        this.status = ReservationStatus.PENDING_PAYMENT;
        this.reservedAt = LocalDateTime.now();
        this.expiresAt = expiresAt;
    }

    public void confirmPayment(LocalDateTime now) {
        if (status == ReservationStatus.CONFIRMED) {
            throw new ConflictException(ErrorCode.ALREADY_CONFIRMED_RESERVATION);
        }

        if (status == ReservationStatus.CANCELED) {
            throw new ConflictException(ErrorCode.ALREADY_CANCELED_RESERVATION);
        }

        if (status == ReservationStatus.EXPIRED || isExpired(now)) {
            this.status = ReservationStatus.EXPIRED;
            throw new ConflictException(ErrorCode.PAYMENT_WINDOW_EXPIRED);
        }

        this.status = ReservationStatus.CONFIRMED;
        this.expiresAt = null;
    }

    public void cancel(LocalDateTime now) {
        if (status == ReservationStatus.CANCELED) {
            throw new ConflictException(ErrorCode.ALREADY_CANCELED_RESERVATION);
        }

        if (status == ReservationStatus.EXPIRED || isExpired(now)) {
            this.status = ReservationStatus.EXPIRED;
            throw new ConflictException(ErrorCode.PAYMENT_WINDOW_EXPIRED);
        }

        this.status = ReservationStatus.CANCELED;
        this.expiresAt = null;
    }

    public boolean expire(LocalDateTime now) {
        if (status != ReservationStatus.PENDING_PAYMENT || !isExpired(now)) {
            return false;
        }

        this.status = ReservationStatus.EXPIRED;
        return true;
    }

    public boolean isExpired(LocalDateTime now) {
        return expiresAt != null && !expiresAt.isAfter(now);
    }
}
