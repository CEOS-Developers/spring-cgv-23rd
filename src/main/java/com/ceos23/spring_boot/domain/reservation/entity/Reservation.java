package com.ceos23.spring_boot.domain.reservation.entity;

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

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLRestriction("is_deleted = false")
public class Reservation extends BaseTimeEntity {
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

    @Builder
    public Reservation(User user, Schedule schedule, ReservationStatus status, Integer totalPrice) {
        this.user = user;
        this.schedule = schedule;
        this.status = status;
        this.totalPrice = totalPrice;
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
