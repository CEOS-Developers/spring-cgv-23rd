package com.ceos.spring_cgv_23rd.domain.reservation.entity;

import com.ceos.spring_cgv_23rd.domain.guest.adapter.out.persistence.entity.GuestEntity;
import com.ceos.spring_cgv_23rd.domain.reservation.enums.ReservationStatus;
import com.ceos.spring_cgv_23rd.domain.screening.adapter.out.persistence.entity.ScreeningEntity;
import com.ceos.spring_cgv_23rd.domain.user.adapter.out.persistence.entity.UserEntity;
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
    private UserEntity userEntity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "guest_id")
    private GuestEntity guestEntity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "screening_id", nullable = false)
    private ScreeningEntity screeningEntity;

    @Column(name = "reservation_number", nullable = false, unique = true)
    private String reservationNumber;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private ReservationStatus status;

    @Column(name = "total_price", nullable = false)
    private Integer totalPrice;


    private Reservation(UserEntity userEntity, GuestEntity guestEntity, ScreeningEntity screeningEntity, String reservationNumber, int seatSize) {
        this.userEntity = userEntity;
        this.guestEntity = guestEntity;
        this.screeningEntity = screeningEntity;
        this.reservationNumber = reservationNumber;
        this.status = ReservationStatus.COMPLETED;
        this.totalPrice = screeningEntity.getPrice() * seatSize;
    }

    public static Reservation createReservation(UserEntity userEntity, ScreeningEntity screeningEntity, int seatSize, String reservationNumber) {
        return new Reservation(userEntity, null, screeningEntity, reservationNumber, seatSize);
    }

    public static Reservation createGuestReservation(GuestEntity guestEntity, ScreeningEntity screeningEntity, int seatSize, String reservationNumber) {
        return new Reservation(null, guestEntity, screeningEntity, reservationNumber, seatSize);
    }

    public void cancel() {
        this.status = ReservationStatus.CANCELLED;
    }

    public boolean isGuest() {
        return userEntity == null && guestEntity != null;
    }
}
