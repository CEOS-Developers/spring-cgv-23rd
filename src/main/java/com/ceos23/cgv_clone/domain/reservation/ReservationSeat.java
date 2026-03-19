package com.ceos23.cgv_clone.domain.reservation;

import com.ceos23.cgv_clone.domain.movie.Schedule;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(
        name = "reservation_seats",
        uniqueConstraints = @UniqueConstraint(
                columnNames = {"schedule_id", "seat_row", "seat_col"}
        )
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class ReservationSeat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reservation_seat_id")
    private Long id;

    @Column(nullable = false)
    private char seatRow;

    @Column(nullable = false)
    private int seatCol;

    @JoinColumn(name = "reservation_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Reservation reservation;

    @JoinColumn(name = "schedule_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Schedule schedule;

    @Builder
    public ReservationSeat(char seatRow, int seatCol, Reservation reservation, Schedule schedule) {
        this.seatRow = seatRow;
        this.seatCol = seatCol;
        this.reservation = reservation;
        this.schedule = schedule;
    }
}
