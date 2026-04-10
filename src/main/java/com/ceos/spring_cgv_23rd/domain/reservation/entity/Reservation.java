package com.ceos.spring_cgv_23rd.domain.reservation.entity;

import com.ceos.spring_cgv_23rd.domain.guest.entity.Guest;
import com.ceos.spring_cgv_23rd.domain.reservation.enums.ReservationStatus;
import com.ceos.spring_cgv_23rd.domain.screening.entity.Screening;
import com.ceos.spring_cgv_23rd.domain.user.entity.User;
import com.ceos.spring_cgv_23rd.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "reservation", indexes = {
        @Index(name = "idx_reservation_screening_status", columnList = "screening_id, status")
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Reservation extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reservation_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "guest_id")
    private Guest guest;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "screening_id", nullable = false)
    private Screening screening;

    @Column(name = "reservation_number", nullable = false, unique = true)
    private String reservationNumber;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private ReservationStatus status;

    @Column(name = "total_price", nullable = false)
    private Integer totalPrice;


    private Reservation(User user, Guest guest, Screening screening, String reservationNumber, int seatSize) {
        this.user = user;
        this.guest = guest;
        this.screening = screening;
        this.reservationNumber = reservationNumber;
        this.status = ReservationStatus.COMPLETED;
        this.totalPrice = screening.getPrice() * seatSize;
    }

    public static Reservation createReservation(User user, Screening screening, int seatSize, String reservationNumber) {
        return new Reservation(user, null, screening, reservationNumber, seatSize);
    }

    public static Reservation createGuestReservation(Guest guest, Screening screening, int seatSize, String reservationNumber) {
        return new Reservation(null, guest, screening, reservationNumber, seatSize);
    }

    public void cancel() {
        this.status = ReservationStatus.CANCELLED;
    }

    public boolean isGuest() {
        return user == null && guest != null;
    }
}
