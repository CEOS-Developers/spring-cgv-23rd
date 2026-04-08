package com.ceos23.spring_boot.domain.reservation.entity;

import com.ceos23.spring_boot.domain.theater.entity.Seat;
import com.ceos23.spring_boot.domain.user.entity.User;
import com.ceos23.spring_boot.global.common.BaseSoftDeleteEntity;
import com.ceos23.spring_boot.global.common.BaseTimeEntity;
import com.ceos23.spring_boot.global.exception.BusinessException;
import com.ceos23.spring_boot.global.exception.ErrorCode;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLRestriction("deleted_at IS NULL")
public class Reservation extends BaseSoftDeleteEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reservation_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "schedule_id", nullable = false)
    private Schedule schedule;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ReservationStatus status;

    @Column(name = "total_price")
    private Integer totalPrice;

    @OneToMany(mappedBy = "reservation", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReservedSeat> reservedSeats = new ArrayList<>();

    @Builder
    public Reservation(User user, Schedule schedule, ReservationStatus status, Integer totalPrice) {
        this.user = user;
        this.schedule = schedule;
        this.status = status;
        this.totalPrice = totalPrice;
    }

    public static Reservation create(User user, Schedule schedule, List<Seat> seats) {
        Reservation reservation = Reservation.builder()
                .user(user)
                .schedule(schedule)
                .status(ReservationStatus.RESERVED)
                .build();

        int totalPrice = 0;
        for (Seat seat : seats) {
            ReservedSeat reservedSeat = ReservedSeat.create(reservation, schedule, seat);
            reservation.addReservedSeat(reservedSeat);

            totalPrice += reservedSeat.getPrice();
        }

        reservation.totalPrice = totalPrice;
        return reservation;
    }

    private void addReservedSeat(ReservedSeat reservedSeat) {
        this.reservedSeats.add(reservedSeat);
        reservedSeat.updateReservation(this);
    }


    public void validateCancelable(){
        LocalDateTime currentTime = LocalDateTime.now();

        if (ReservationStatus.CANCELED == this.status)
            throw new BusinessException(ErrorCode.ALREADY_CANCELED_RESERVATION);

        if (currentTime.isAfter(schedule.getStartTime().minusMinutes(15)))
            throw new BusinessException(ErrorCode.CANCELLATION_DEADLINE_PASSED);
    }

    public void cancel() {
        this.status = ReservationStatus.CANCELED;
    }
}
