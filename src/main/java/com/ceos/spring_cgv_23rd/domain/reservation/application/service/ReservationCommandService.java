package com.ceos.spring_cgv_23rd.domain.reservation.application.service;


import com.ceos.spring_cgv_23rd.domain.payment.application.dto.command.PayCommand;
import com.ceos.spring_cgv_23rd.domain.payment.application.dto.result.PaymentResult;
import com.ceos.spring_cgv_23rd.domain.payment.application.port.in.CancelPaymentUseCase;
import com.ceos.spring_cgv_23rd.domain.payment.application.port.in.PaymentUseCase;
import com.ceos.spring_cgv_23rd.domain.reservation.application.dto.command.ConfirmReservationCommand;
import com.ceos.spring_cgv_23rd.domain.reservation.application.dto.command.CreateReservationCommand;
import com.ceos.spring_cgv_23rd.domain.reservation.application.dto.result.ReservationDetailResult;
import com.ceos.spring_cgv_23rd.domain.reservation.application.dto.result.ReservationResult;
import com.ceos.spring_cgv_23rd.domain.reservation.application.dto.result.ScreeningInfoResult;
import com.ceos.spring_cgv_23rd.domain.reservation.application.dto.result.SeatInfoResult;
import com.ceos.spring_cgv_23rd.domain.reservation.application.port.in.ConfirmReservationUseCase;
import com.ceos.spring_cgv_23rd.domain.reservation.application.port.in.CreateReservationUseCase;
import com.ceos.spring_cgv_23rd.domain.reservation.application.port.out.ReservationPersistencePort;
import com.ceos.spring_cgv_23rd.domain.reservation.application.port.out.ScreeningPort;
import com.ceos.spring_cgv_23rd.domain.reservation.application.port.out.SeatHoldPort;
import com.ceos.spring_cgv_23rd.domain.reservation.application.port.out.SeatPort;
import com.ceos.spring_cgv_23rd.domain.reservation.domain.Reservation;
import com.ceos.spring_cgv_23rd.domain.reservation.exception.ReservationErrorCode;
import com.ceos.spring_cgv_23rd.global.apiPayload.exception.GeneralException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.ceos.spring_cgv_23rd.domain.reservation.domain.ReservationPolicy.HOLD_TTL_SECONDS;

@Service
@Slf4j
@RequiredArgsConstructor
public class ReservationCommandService implements CreateReservationUseCase, ConfirmReservationUseCase {

    private final ReservationPersistencePort reservationPersistencePort;
    private final ScreeningPort screeningPort;
    private final SeatPort seatPort;
    private final SeatHoldPort seatHoldPort;
    private final ReservationTxService reservationTxService;
    private final PaymentUseCase paymentUseCase;
    private final CancelPaymentUseCase cancelPaymentUseCase;

    @Override
    public ReservationResult createReservation(Long userId, CreateReservationCommand command) {
        // 좌석 유효성 검증
        validateSeats(command.screeningId(), command.seatIds());

        // 예매 임시 토큰 발급 및 좌석 선점
        String holdToken = UUID.randomUUID().toString();
        String holderKey = "user:" + userId + ":" + holdToken;
        holdOrThrow(command.screeningId(), command.seatIds(), holderKey);

        return new ReservationResult(holdToken, command.screeningId(), command.seatIds(), LocalDateTime.now().plusSeconds(HOLD_TTL_SECONDS));
    }

    @Override
    public ReservationDetailResult confirmReservation(ConfirmReservationCommand command) {
        // 좌석 선점 유효성 검증
        verifyHold(command.screeningId(), command.seatIds(), "user:" + command.userId() + ":" + command.reservationToken());

        // 상영 정보 조회
        ScreeningInfoResult screeningInfo = findScreeningInfoOrThrow(command.screeningId());

        // 좌석 상세 정보 조회
        Map<Long, SeatInfoResult> seatInfoMap = seatPort.findSeatInfoByIdsAndHallTypeId(command.seatIds(), screeningInfo.hallTypeId());

        // 결제 총액 및 주문명
        int amount = Reservation.calculateTotalPrice(screeningInfo.price(), command.seatIds().size());
        String orderName = Reservation.generateOrderName(screeningInfo.movieTitle(), command.seatIds().size());

        // 외부 PG 결제 승인 요청
        PaymentResult payment = paymentUseCase.pay(
                new PayCommand(command.reservationToken(), orderName, amount));

        // 좌석 차감 및 예매 상태 확정
        Reservation savedReservation;
        try {
            savedReservation = reservationTxService.persistConfirmed(
                    command.userId(), command.screeningId(), screeningInfo.price(),
                    command.reservationToken(), command.seatIds());
        } catch (Exception e) {
            log.warn("예매 실패. 보상 결제 취소 시도. paymentId={}", command.reservationToken(), e);

            // 저장 실패시 이미 승인된 PG 결제 롤백
            try {
                cancelPaymentUseCase.cancel(command.reservationToken());
            } catch (Exception ex) {
                log.error("보상 결제 취소 실패. paymentId={}", command.reservationToken(), ex);
            }
            throw new GeneralException(ReservationErrorCode.CONFIRM_FAILED_ROLLED_BACK);
        }

        // 성공 여부와 무관하게 선점된 좌석 Hold 해제
        try {
            seatHoldPort.releaseSeats(command.screeningId(), command.seatIds());
        } catch (Exception e) {
            log.warn("seat hold release 실패. screeningId={}, seatIds={}", command.screeningId(), command.seatIds(), e);
        }

        // 최종 예매 확정 상세 결과 반환
        return ReservationDetailResult.of(savedReservation, screeningInfo, seatInfoMap, payment);
    }


    // 상영 정보 조회
    private ScreeningInfoResult findScreeningInfoOrThrow(Long screeningId) {
        return screeningPort.findScreeningInfoById(screeningId)
                .orElseThrow(() -> new GeneralException(ReservationErrorCode.SCREENING_NOT_FOUND));
    }

    // 좌석 검증 및 중복 예약 방지
    private void validateSeats(Long screeningId, List<Long> seatIds) {
        // 상영 정보 조회
        ScreeningInfoResult screeningInfo = findScreeningInfoOrThrow(screeningId);

        // 좌석 존재 여부 조회
        Map<Long, SeatInfoResult> seatInfoMap = seatPort.findSeatInfoByIdsAndHallTypeId(seatIds, screeningInfo.hallTypeId());
        if (seatInfoMap.size() != seatIds.size()) {
            throw new GeneralException(ReservationErrorCode.SEAT_NOT_FOUND);
        }

        // 이미 예약 완료된 좌석인지 검증
        List<Long> reservedSeatIds = reservationPersistencePort.findReservedSeatIdsByScreeningId(screeningId);
        boolean reserved = seatIds.stream().anyMatch(reservedSeatIds::contains);
        if (reserved) {
            throw new GeneralException(ReservationErrorCode.SEAT_ALREADY_RESERVED);
        }

    }

    // 좌석 선점
    private void holdOrThrow(Long screeningId, List<Long> seatIds, String holerKey) {
        boolean success = seatHoldPort.holdSeats(screeningId, seatIds, holerKey, HOLD_TTL_SECONDS);
        if (!success) {
            throw new GeneralException(ReservationErrorCode.SEAT_ALREADY_RESERVED);
        }
    }

    // 좌석 선점 유효성 검증
    private void verifyHold(Long screeningId, List<Long> seatIds, String holerKey) {
        if (!seatHoldPort.isHeldByUser(screeningId, seatIds, holerKey)) {
            throw new GeneralException(ReservationErrorCode.SEAT_HOLD_EXPIRED);
        }
    }
}
