package com.ceos.spring_cgv_23rd.domain.reservation.entity;

import com.ceos.spring_cgv_23rd.domain.theater.entity.Seat;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "reservation_seat")
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReservationSeat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reservation_seat_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reservation_id", nullable = false)
    private Reservation reservation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seat_id", nullable = false)
    private Seat seat;


    public static ReservationSeat createReservationSeat(Reservation reservation, Seat seat) {
        return ReservationSeat.builder()
                .reservation(reservation)
                .seat(seat)
                .build();
    }
}
