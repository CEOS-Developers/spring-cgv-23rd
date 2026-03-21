package com.ceos23.spring_cgv_23rd.Reservation.Domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReservationSeat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @JoinColumn(name = "reservation_id")
    private Reservation reservation;

    private String seatName;

    @Enumerated(EnumType.STRING)
    private SeatInfo seatInfo;

    public void addReservation(Reservation reservation){
        this.reservation = reservation;
        reservation.getReservationSeats().add(this);
    }
}

