package com.ceos23.spring_boot.cgv.domain.reservation;

import com.ceos23.spring_boot.cgv.domain.cinema.SeatTemplate;
import com.ceos23.spring_boot.cgv.domain.movie.Screening;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_screening_seat_template",
                        columnNames = {"screening_id", "seat_template_id"}
                )
        }
)
public class ReservationSeat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reservation_id", nullable = false)
    private Reservation reservation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "screening_id", nullable = false)
    private Screening screening;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seat_template_id", nullable = false)
    private SeatTemplate seatTemplate;

    public ReservationSeat(Reservation reservation, Screening screening, SeatTemplate seatTemplate) {
        this.reservation = reservation;
        this.screening = screening;
        this.seatTemplate = seatTemplate;
    }
}