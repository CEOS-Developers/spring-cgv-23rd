package com.ceos23.cgv.domain.reservation.entity;

import com.ceos23.cgv.domain.movie.entity.Screening;
import com.ceos23.cgv.domain.reservation.enums.Payment;
import com.ceos23.cgv.domain.reservation.enums.ReservationStatus;
import com.ceos23.cgv.domain.user.entity.User;
import com.ceos23.cgv.global.entity.BaseTimeEntity;
import com.ceos23.cgv.global.exception.CustomException;
import com.ceos23.cgv.global.exception.ErrorCode;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "reservations")
public class Reservation extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reservation_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "screening_id", nullable = false)
    private Screening screening;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReservationStatus status;

    @Column(nullable = false)
    private int totalPrice;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Payment payment;

    // 양방향 매핑 및 영속성 전이(Cascade) 설정 추가
    @OneToMany(mappedBy = "reservation", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReservedSeat> reservedSeats = new ArrayList<>();

    @Builder
    private Reservation(User user, Screening screening, Payment payment) {
        this.user = user;
        this.screening = screening;
        this.payment = payment;
        this.status = ReservationStatus.BOOKED; // 초기 상태 강제
    }

    // 정적 팩토리 메서드 (생성 캡슐화)
    public static Reservation create(User user, Screening screening, Payment payment, List<String> seatNumbers) {
        Reservation reservation = Reservation.builder()
                .user(user)
                .screening(screening)
                .payment(payment)
                .build();

        // 도메인 내부에서 연관관계 및 계산 로직 호출
        reservation.reserveSeats(seatNumbers);
        return reservation;
    }

    // 좌석 할당 및 검증 로직
    private void reserveSeats(List<String> seatNumbers) {
        if (seatNumbers == null || seatNumbers.isEmpty()) {
            throw new CustomException(ErrorCode.INVALID_INPUT_VALUE);
        }
        for (String seatNumber : seatNumbers) {
            this.reservedSeats.add(new ReservedSeat(this, seatNumber));
        }
        calculateTotalPrice();
    }

    // 3. 도메인 규칙 내재화 (가격 계산)
    private void calculateTotalPrice() {
        final int TICKET_PRICE = 15000; // 추후 DB나 정책 클래스에서 주입받도록 확장 가능 (OCP)
        this.totalPrice = this.reservedSeats.size() * TICKET_PRICE;
    }

    public void cancel() {
        if (this.status == ReservationStatus.CANCELED) {
            throw new CustomException(ErrorCode.RESERVATION_ALREADY_CANCELED);
        }
        this.status = ReservationStatus.CANCELED;
        this.reservedSeats.clear();
    }
}
