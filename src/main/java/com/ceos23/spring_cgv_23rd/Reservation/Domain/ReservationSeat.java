package com.ceos23.spring_cgv_23rd.Reservation.Domain;

import com.ceos23.spring_cgv_23rd.Screen.Domain.Screen;
import com.ceos23.spring_cgv_23rd.Screen.Domain.Screening;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Getter
@Table(name = "ReservationSeat",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "SCREENING_SEATNUMBER_UNIQUE",
                        columnNames = {"screening_id", "seat_name", "active_key"}
                )
        }
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReservationSeat {
    private ReservationSeat(Reservation res, String seatName, SeatInfo seatInfo, int price, Screening screening){
        this.reservation = res;
        this.seatName = seatName;
        this.seatInfo = seatInfo;
        this.price = price;
        this.screening = screening;
        this.reservationStatus = ReservationStatus.RESERVED;
        this.activeKey = "ACTIVE";
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Setter
    @ManyToOne
    @JoinColumn(name = "reservation_id")
    private Reservation reservation;

    @Enumerated(EnumType.STRING)
    private ReservationStatus reservationStatus;

    private String activeKey;

    @ManyToOne
    @JoinColumn(name = "screening_id")
    private Screening screening;

    @Column(name = "seat_name")
    private String seatName;

    private int price;

    @Enumerated(EnumType.STRING)
    private SeatInfo seatInfo;

    public void addReservation(Reservation reservation){
        this.reservation = reservation;
        reservation.getReservationSeats().add(this);
    }

    /**
     * TODO: 좌석 조회 시 status 확인하도록하기
     */
    public void cancel(){
        reservationStatus = ReservationStatus.CANCELED;
        this.activeKey = UUID.randomUUID().toString();
    }

    public static ReservationSeat create(Reservation rs, String seatName, SeatInfo seatInfo,
                                         int price, Screening screening) {
        return new ReservationSeat(rs, seatName, seatInfo, price, screening);
    }
}

