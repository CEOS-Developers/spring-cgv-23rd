package com.ceos23.spring_cgv_23rd.Reservation.Domain;

import com.ceos23.spring_cgv_23rd.Screen.Domain.Screen;
import com.ceos23.spring_cgv_23rd.Screen.Domain.Screening;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReservationSeat {
    private ReservationSeat(Reservation res, String seatName, SeatInfo seatInfo, int price, Screening screening){
        this.reservation = res;
        this.seatName = seatName;
        this.seatInfo = seatInfo;
        this.price = price;
        this.screening = screening;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Setter
    @ManyToOne
    @JoinColumn(name = "reservation_id")
    private Reservation reservation;

    @ManyToOne
    @JoinColumn(name = "screening_id")
    private Screening screening;

    private String seatName;

    private int price;

    private boolean occupied;

    @Enumerated(EnumType.STRING)
    private SeatInfo seatInfo;

    public void addReservation(Reservation reservation){
        this.reservation = reservation;
        reservation.getReservationSeats().add(this);
    }

    public static ReservationSeat create(Reservation rs, String seatName, SeatInfo seatInfo,
                                         int price, Screening screening){
        ReservationSeat rss = new ReservationSeat(rs, seatName, seatInfo, price, screening);
        rss.occupied = true;
        return rss;
    }
}

