package com.ceos.spring_boot.domain.reservation.entity;

import com.ceos.spring_boot.domain.cinema.entity.Seat;
import com.ceos.spring_boot.domain.schedule.entity.Schedule;
import com.ceos.spring_boot.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@AllArgsConstructor
@Table( // 특정 상영 일정에 특정 좌석은 1쌍만 존재해야함
        name = "reservation_seat",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_schedule_seat",
                        columnNames = {"schedule_id", "seat_id"}
                )
        }
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReservationSeat extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reservation_seat_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reservation_id", nullable = false)
    private Reservation reservation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "schedule_id", nullable = false)
    private Schedule schedule;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seat_id", nullable = false)
    private Seat seat; // 예매한 좌석

    public static ReservationSeat create(Reservation reservation, Seat seat, Schedule schedule) {
        ReservationSeat rs = ReservationSeat.builder()
                .reservation(reservation)
                .seat(seat)
                .schedule(schedule)
                .build();

        reservation.addReservationSeat(rs);
        return rs;
    }
}