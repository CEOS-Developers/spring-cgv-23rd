package com.ceos23.cgv_clone.domain.reservation;

import com.ceos23.cgv_clone.domain.movie.Schedule;
import com.ceos23.cgv_clone.domain.user.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


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
    private int price;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReservationStatus status;

    @JoinColumn(name = "user_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @JoinColumn(name = "schedule_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Schedule schedule;

    @Builder
    public Reservation(LocalDateTime reservedAt, int price, ReservationStatus status, User user, Schedule schedule) {
        this.reservedAt = reservedAt;
        this.price = price;
        this.status = status;
        this.user = user;
        this.schedule = schedule;
    }
}
