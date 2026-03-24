package com.ceos23.spring_boot.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Reservation {

    @Id @GeneratedValue
    private Long id;

    @ManyToOne
    private User user;

    @ManyToOne
    private Screening screening;

    @ManyToOne
    private Seat seat;

    public Reservation(User user, Screening screening, Seat seat) {
        this.user = user;
        this.screening = screening;
        this.seat = seat;
    }
}