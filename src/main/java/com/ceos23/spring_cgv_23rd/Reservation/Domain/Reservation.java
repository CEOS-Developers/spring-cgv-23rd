package com.ceos23.spring_cgv_23rd.Reservation.Domain;

import com.ceos23.spring_cgv_23rd.global.DiscountPolicy.DiscountPolicy;
import com.ceos23.spring_cgv_23rd.Screen.Domain.Screening;
import com.ceos23.spring_cgv_23rd.User.Domain.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Reservation {
    private Reservation(User user, Screening sc, LocalDateTime date){
        this.user = user;
        this.screening = sc;
        this.reservationDate = date;
        this.status = ReservationStatus.RESERVED;
        this.expiredAt = LocalDateTime.now().plusMinutes(5);
    }

    @Id
    @Getter
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Getter
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Getter
    @ManyToOne
    @JoinColumn(name = "screening_id")
    private Screening screening;

    @Getter
    @OneToMany(mappedBy = "reservation", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReservationSeat> reservationSeats = new ArrayList<>();

    @Getter
    private LocalDateTime reservationDate;

    @Getter
    private int totalPrice;

    private LocalDateTime expiredAt;

    private boolean expired;

    @Getter
    @Enumerated(value = EnumType.STRING)
    private ReservationStatus status;

    public boolean canPay(){
        return status.equals(ReservationStatus.RESERVED);
    }

    public void expired(){
        this.expired = true;
        this.status = ReservationStatus.EXPIRED;

        for (ReservationSeat rs : reservationSeats){
            rs.cancel();
        }
    }

    public void addReservationSeat(ReservationSeat rs){
        reservationSeats.add(rs);
        rs.setReservation(this);
    }

    private void computeTotalPrice(){
        int totalSum = 0;
        for (ReservationSeat rs : reservationSeats){
            totalSum += rs.getPrice();
        }

        this.totalPrice = totalSum;
    }

    public void cancel(){
        this.status = ReservationStatus.CANCELED;

        for (ReservationSeat seat : reservationSeats){
            seat.cancel();
        }
    }

    public static Reservation reserve(User user,
                                     Screening screening,
                                     Map<String, SeatInfo> reservingSeats,
                                     DiscountPolicy discountPolicy){
        Reservation res = new Reservation(user, screening, screening.getStartTime());

        for (String seatName : reservingSeats.keySet()){
            ReservationSeat rss = ReservationSeat.create(
                    res, seatName, reservingSeats.get(seatName), screening, discountPolicy
            );
            res.addReservationSeat(rss);
        }
        res.computeTotalPrice();

        return res;
    }

    public void buyReservation(){
        this.status = ReservationStatus.PAID;
    }
}
