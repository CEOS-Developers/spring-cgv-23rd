package com.ceos23.cgv.domain.reservation.service;

import com.ceos23.cgv.domain.cinema.enums.TheaterType;
import com.ceos23.cgv.domain.movie.entity.Screening;
import com.ceos23.cgv.domain.movie.repository.ScreeningRepository;
import com.ceos23.cgv.domain.reservation.dto.ReservationCreateRequest;
import com.ceos23.cgv.domain.reservation.dto.ReservationResponse;
import com.ceos23.cgv.domain.reservation.entity.Reservation;
import com.ceos23.cgv.domain.reservation.enums.Payment;
import com.ceos23.cgv.domain.reservation.enums.ReservationStatus;
import com.ceos23.cgv.domain.reservation.repository.ReservationRepository;
import com.ceos23.cgv.domain.reservation.repository.ReservedSeatRepository;
import com.ceos23.cgv.domain.user.entity.User;
import com.ceos23.cgv.domain.user.repository.UserRepository;
import com.ceos23.cgv.global.exception.CustomException;
import com.ceos23.cgv.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final UserRepository userRepository;
    private final ScreeningRepository screeningRepository;
    private final ReservedSeatRepository reservedSeatRepository;

    // 전역적으로 관리할 할인 금액 상수 선언
    private static final int MORNING_DISCOUNT = 4000;

    /**
     * 예매 생성 (Rich Domain 활용)
     */
    @Transactional
    public ReservationResponse createReservation(Long userId, ReservationCreateRequest request) {

        // 1. 조회 (Data Loading)
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        Screening screening = screeningRepository.findById(request.screeningId())
                .orElseThrow(() -> new CustomException(ErrorCode.SCREENING_NOT_FOUND));

        // 2. 도메인 객체에 생성 및 비즈니스 로직 위임 (Rich Domain)
        Reservation reservation = Reservation.create(
                user,
                screening,
                request.payment(),
                request.seatNumbers()
        );

        try {
            // 3. 저장 및 반환 (Cascade 설정으로 인해 ReservedSeat들도 자동으로 함께 INSERT 됨)
            reservationRepository.saveAndFlush(reservation);
        } catch (DataIntegrityViolationException e) {
            throw new CustomException(ErrorCode.SEAT_ALREADY_RESERVED);
        }

        return ReservationResponse.from(reservation);
    }

    /**
     * 쿠폰 코드에 따른 할인 금액 계산 로직
     */
    private int applyCouponDiscount(int currentPrice, String couponCode) {
        int discountAmount = 0;

        // 임시 쿠폰 비즈니스 로직 (향후 Coupon 엔티티가 생기면 DB 조회로 변경 가능)
        if (couponCode.equals("WELCOME_CGV")){
            discountAmount = 3000; // 3,000원 할인
        } else if (couponCode.equals("VIP_HALF_PRICE")){
            discountAmount = currentPrice / 2; // 반값 할인
        } else {
            throw new IllegalArgumentException("유효하지 않은 쿠폰 코드입니다.");
        }

        // 할인 적용 (결제 금액이 0원 밑으로 떨어지지 않도록 방어 로직)
        int finalPrice = currentPrice - discountAmount;
        return Math.max(finalPrice, 0);
    }

    /**
     * 영화 예매 취소 로직
     */
    @Transactional
    public void cancelReservation(Long userId, Long reservationId) {
        // 1. 예매 내역 조회
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new IllegalArgumentException("예매 내역을 찾을 수 없습니다."));

        // 2. 권한 검증: 본인의 예매 내역이 맞는지 확인
        if (!reservation.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("본인의 예매 내역만 취소할 수 있습니다.");
        }

        // 3. 상태 검증: 이미 취소된 예매인지 확인
        if (reservation.getStatus() == ReservationStatus.CANCELED) {
            throw new IllegalStateException("이미 취소 처리된 예매입니다.");
        }

        // 4. 예매를 취소하면 점유했던 좌석 데이터도 모두 삭제하여 빈자리로 만듭니다!
        reservedSeatRepository.deleteAllByReservationId(reservationId);

        // 5. 취소 상태로 변경 (이후 @Transactional에 의해 DB에 자동 반영됨 - Dirty Checking)
        reservation.cancel();
    }
}
