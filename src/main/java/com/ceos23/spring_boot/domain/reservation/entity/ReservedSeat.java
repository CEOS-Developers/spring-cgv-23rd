package com.ceos23.spring_boot.domain.reservation.entity;

import com.ceos23.spring_boot.domain.theater.entity.Seat;
import com.ceos23.spring_boot.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_schedule_seat",
            columnNames = {"schedule_id", "seat_id"}
        )
    }
)
public class ReservedSeat extends BaseEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reserved_seat_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reservation_id", nullable = false)
    private Reservation reservation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seat_id", nullable = false)
    private Seat seat;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "schedule_id", nullable = false)
    private Schedule schedule;

    @Column(precision = 10, scale = 2)
    private BigDecimal price;

    @Builder
    public ReservedSeat(Reservation reservation, Seat seat, Schedule schedule, BigDecimal price) {
        this.reservation = reservation;
        this.seat = seat;
        this.schedule = schedule;
        this.price = price;
    }
}
