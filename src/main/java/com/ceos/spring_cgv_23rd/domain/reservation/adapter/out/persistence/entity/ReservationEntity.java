package com.ceos.spring_cgv_23rd.domain.reservation.adapter.out.persistence.entity;

import com.ceos.spring_cgv_23rd.domain.reservation.domain.ReservationStatus;
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
public class ReservationEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reservation_id")
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "guest_id")
    private Long guestId;

    @Column(name = "screening_id", nullable = false)
    private Long screeningId;

    @Column(name = "reservation_number", nullable = false, unique = true)
    private String reservationNumber;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private ReservationStatus status;

    @Column(name = "total_price", nullable = false)
    private Integer totalPrice;


    private ReservationEntity(Long userId, Long guestId, Long screeningId,
                              String reservationNumber, ReservationStatus status, Integer totalPrice) {
        this.userId = userId;
        this.guestId = guestId;
        this.screeningId = screeningId;
        this.reservationNumber = reservationNumber;
        this.status = status;
        this.totalPrice = totalPrice;
    }

    public static ReservationEntity createForUser(Long userId, Long screeningId,
                                                  String reservationNumber, ReservationStatus status, Integer totalPrice) {
        return new ReservationEntity(userId, null, screeningId, reservationNumber, status, totalPrice);
    }

    public static ReservationEntity createForGuest(Long guestId, Long screeningId,
                                                   String reservationNumber, ReservationStatus status, Integer totalPrice) {
        return new ReservationEntity(null, guestId, screeningId, reservationNumber, status, totalPrice);
    }

    public void updateStatus(ReservationStatus status) {
        this.status = status;
    }
}
