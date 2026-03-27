package com.ceos23.spring_cgv_23rd.Reservation.Domain;

import com.ceos23.spring_cgv_23rd.DiscountPolicy.DiscountPolicy;
import com.ceos23.spring_cgv_23rd.Screen.Domain.Screening;
import com.ceos23.spring_cgv_23rd.User.Domain.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Reservation {
    private Reservation(User user, Screening sc, LocalDateTime date){
        this.user = user;
        this.screening = sc;
        this.reservationDate = date;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "screening_id")
    private Screening screening;

    @OneToMany(mappedBy = "reservation", cascade = CascadeType.ALL)
    private List<ReservationSeat> reservationSeats = new ArrayList<>();

    private LocalDateTime reservationDate;

    private int totalPrice;

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

    public static Reservation create(User user,
                                     Screening screening,
                                     Map<String, SeatInfo> reservingSeats,
                                     DiscountPolicy discountPolicy){
        Reservation res = new Reservation(user, screening, screening.getStartTime());

        for (String seatName : reservingSeats.keySet()){
            int price = screening.getMoviePrice() - discountPolicy.calculateDiscount(screening, reservingSeats.get(seatName));

            System.out.println("가격: " + price);

            ReservationSeat rss = ReservationSeat.create(
                    res, seatName, reservingSeats.get(seatName), price, screening
            );

            res.addReservationSeat(rss);
        }

        res.computeTotalPrice();

        return res;
    }
}
