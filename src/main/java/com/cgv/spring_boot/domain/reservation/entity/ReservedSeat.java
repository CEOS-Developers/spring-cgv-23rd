package com.cgv.spring_boot.domain.reservation.entity;

import com.cgv.spring_boot.domain.schedule.entity.Schedule;
import com.cgv.spring_boot.global.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@Table(
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_reserved_seat_per_schedule", columnNames = {"seat_row", "seat_col", "schedule_id"})
        }
)
public class ReservedSeat extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "seat_row", nullable = false)
    private String seatRow;

    @Column(name = "seat_col", nullable = false)
    private int seatCol;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "schedule_id", nullable = false)
    private Schedule schedule;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "res_id")
    private Reservation reservation;

    @Builder
    public ReservedSeat(String seatRow, int seatCol, Schedule schedule, Reservation reservation) {
        this.seatRow = seatRow;
        this.seatCol = seatCol;
        this.schedule = schedule;
        this.reservation = reservation;
    }
}
