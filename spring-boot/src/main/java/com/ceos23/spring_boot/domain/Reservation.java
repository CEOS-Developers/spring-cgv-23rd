package com.ceos23.spring_boot.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
                        name = "uk_reservation_screening_seat",
                        columnNames = {"screening_id", "seat_id"}
                )
        }
)
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Screening screening;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Seat seat;

    private Reservation(User user, Screening screening, Seat seat) {
        this.user = user;
        this.screening = screening;
        this.seat = seat;
    }

    public static Reservation create(User user, Screening screening, Seat seat) {
        return new Reservation(user, screening, seat);
    }
}