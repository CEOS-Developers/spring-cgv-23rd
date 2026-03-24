package com.ceos.spring_cgv_23rd.domain.reservation.entity;

import com.ceos.spring_cgv_23rd.domain.reservation.enums.ReservationStatus;
import com.ceos.spring_cgv_23rd.domain.screening.entity.Screening;
import com.ceos.spring_cgv_23rd.domain.user.entity.User;
import com.ceos.spring_cgv_23rd.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "reservation", indexes = {
        @Index(name = "idx_reservation_screening_status", columnList = "screening_id, status")
})
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Reservation extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reservation_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

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


    public static Reservation createReservation(User user, Screening screening, int seatSize) {
        return Reservation.builder()
                .user(user)
                .screening(screening)
                .reservationNumber(UUID.randomUUID().toString().substring(0, 8).toUpperCase())
                .status(ReservationStatus.COMPLETED)
                .totalPrice(screening.getPrice() * seatSize)
                .build();
    }


    public void cancel() {
        this.status = ReservationStatus.CANCELLED;
    }
}
