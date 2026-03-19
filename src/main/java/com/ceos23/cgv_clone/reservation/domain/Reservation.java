package com.ceos23.cgv_clone.reservation.domain;

import com.ceos23.cgv_clone.movie.domain.Schedule;
import com.ceos23.cgv_clone.user.domain.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name = "reservations")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reservation_id")
    private Long id;

    @Column(nullable = false)
    private LocalDateTime reservedAt;

    @Column(nullable = false)
    private int totalPrice;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReservationStatus status;

    @Column(nullable = false)
    private String seatNames;

    @JoinColumn(name = "user_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @JoinColumn(name = "schedule_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Schedule schedule;

    @OneToMany(mappedBy = "reservation", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReservationSeat> reservationSeats = new ArrayList<>();

    @Builder
    public Reservation(LocalDateTime reservedAt, int totalPrice, ReservationStatus status, User user, Schedule schedule, String seatNames) {
        this.reservedAt = reservedAt;
        this.totalPrice = totalPrice;
        this.status = status;
        this.user = user;
        this.schedule = schedule;
        this.seatNames = seatNames;
    }

    public void addReservationSeat(ReservationSeat seat) {
        this.reservationSeats.add(seat);
    }

    public void cancel() {
        this.status = ReservationStatus.CANCELED;
        this.reservationSeats.clear();
    }
}
