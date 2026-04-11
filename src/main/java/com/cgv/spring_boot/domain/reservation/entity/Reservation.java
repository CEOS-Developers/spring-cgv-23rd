package com.cgv.spring_boot.domain.reservation.entity;

import com.cgv.spring_boot.domain.schedule.entity.Schedule;
import com.cgv.spring_boot.domain.user.entity.User;
import com.cgv.spring_boot.global.common.entity.BaseEntity;
import com.cgv.spring_boot.domain.reservation.exception.ReservationErrorCode;
import com.cgv.spring_boot.global.error.exception.BusinessException;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class Reservation extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "res_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReservationStatus status; // BOOKED, CANCELED

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "schedule_id")
    private Schedule schedule;

    @Builder
    public Reservation(ReservationStatus status, User user, Schedule schedule) {
        this.status = status;
        this.user = user;
        this.schedule = schedule;
    }

    public static Reservation create(User user, Schedule schedule) {
        return Reservation.builder()
                .status(ReservationStatus.RESERVED)
                .user(user)
                .schedule(schedule)
                .build();
    }

    /**
     * 예약 상태 변경 (RESERVED -> CANCELLED)
     */
    public void cancelStatus() {
        if (this.status == ReservationStatus.CANCELLED) {
            throw new BusinessException(ReservationErrorCode.ALREADY_CANCELLED);
        }
        this.status = ReservationStatus.CANCELLED;
    }
}
