package com.ceos23.spring_cgv_23rd.Reservation.Domain;

import com.ceos23.spring_cgv_23rd.Screen.Domain.Screening;
import com.ceos23.spring_cgv_23rd.User.Domain.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Reservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "screening_id")
    private Screening screening;

    @Builder.Default
    @OneToMany(mappedBy = "reservation")
    private List<ReservationSeat> reservationSeats = new ArrayList<>();

    private LocalDateTime reservationDate;

    private int totalPrice;

    public void addReservationSeat(ReservationSeat rs){
        reservationSeats.add(rs);
        rs.setReservation(this);
    }
}
