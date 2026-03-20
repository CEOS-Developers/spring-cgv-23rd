package com.ceos.spring_boot.domain.reservation.entity;

import com.ceos.spring_boot.domain.schedule.entity.Schedule;
import com.ceos.spring_boot.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@AllArgsConstructor
@Table(name = "reservations")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reservation_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // 사용자

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "schedule_id", nullable = false)
    private Schedule schedule; // 예매한 상영 일정

    private LocalDateTime reservationDate; // 예매 일시

    @Enumerated(EnumType.STRING)
    private ReservationStatus status; // 예매 상태 (CONFIRMED, CANCELED)

    public void confirm() {
        this.status = ReservationStatus.CONFIRMED;
    }

    public void cancel() {
        this.status = ReservationStatus.CANCELED;
    }

    // 한 번의 예매로 여러 좌석을 선택할 수 있으므로 OneToMany 관계
    @OneToMany(mappedBy = "reservation", cascade = CascadeType.ALL)
    private List<ReservationSeat> reservationSeats = new ArrayList<>();

    public void addReservationSeat(ReservationSeat seat) {
        if (this.reservationSeats == null) {
            this.reservationSeats = new ArrayList<>();
        }
        this.reservationSeats.add(seat);
    }
}
