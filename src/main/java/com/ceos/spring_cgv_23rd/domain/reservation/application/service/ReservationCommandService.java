package com.ceos.spring_cgv_23rd.domain.reservation.application.service;


import com.ceos.spring_cgv_23rd.domain.reservation.application.dto.command.CancelGuestReservationCommand;
import com.ceos.spring_cgv_23rd.domain.reservation.application.dto.command.CreateGuestReservationCommand;
import com.ceos.spring_cgv_23rd.domain.reservation.application.dto.command.CreateReservationCommand;
import com.ceos.spring_cgv_23rd.domain.reservation.application.dto.result.GuestInfoResult;
import com.ceos.spring_cgv_23rd.domain.reservation.application.dto.result.ReservationDetailResult;
import com.ceos.spring_cgv_23rd.domain.reservation.application.dto.result.ScreeningInfoResult;
import com.ceos.spring_cgv_23rd.domain.reservation.application.dto.result.SeatInfoResult;
import com.ceos.spring_cgv_23rd.domain.reservation.application.port.in.CancelReservationUseCase;
import com.ceos.spring_cgv_23rd.domain.reservation.application.port.in.CreateReservationUseCase;
import com.ceos.spring_cgv_23rd.domain.reservation.application.port.out.GuestPort;
import com.ceos.spring_cgv_23rd.domain.reservation.application.port.out.ReservationPersistencePort;
import com.ceos.spring_cgv_23rd.domain.reservation.application.port.out.ScreeningPort;
import com.ceos.spring_cgv_23rd.domain.reservation.application.port.out.SeatPort;
import com.ceos.spring_cgv_23rd.domain.reservation.domain.Reservation;
import com.ceos.spring_cgv_23rd.domain.reservation.exception.ReservationErrorCode;
import com.ceos.spring_cgv_23rd.global.apiPayload.exception.GeneralException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReservationCommandService implements CreateReservationUseCase, CancelReservationUseCase {

    private final ReservationPersistencePort reservationPersistencePort;
    private final ScreeningPort screeningPort;
    private final SeatPort seatPort;
    private final GuestPort guestPort;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public ReservationDetailResult createReservation(Long userId, CreateReservationCommand command) {

        // 상영 정보 조회
        ScreeningInfoResult screeningInfo = findScreeningInfoOrThrow(command.screeningId());

        // 좌석 검증
        Map<Long, SeatInfoResult> seatInfoMap = validateAndGetSeats(command.seatIds(), command.screeningId(), screeningInfo.hallTypeId());

        // 상영 좌석 차감
        screeningPort.decreaseScreeningSeats(command.screeningId(), command.seatIds().size());

        // 예매 생성
        Reservation reservation = Reservation.createReservation(userId, command.screeningId(), screeningInfo.price(), command.seatIds());
        Reservation savedReservation = reservationPersistencePort.saveReservation(reservation);

        return buildReservationDetailResult(savedReservation, screeningInfo, seatInfoMap);
    }

    @Override
    @Transactional
    public ReservationDetailResult createGuestReservation(CreateGuestReservationCommand command) {

        // 상영 정보 조회
        ScreeningInfoResult screeningInfo = findScreeningInfoOrThrow(command.screeningId());

        // 좌석 검증
        Map<Long, SeatInfoResult> seatInfoMap = validateAndGetSeats(command.seatIds(), command.screeningId(), screeningInfo.hallTypeId());

        // 상영 좌석 차감
        screeningPort.decreaseScreeningSeats(command.screeningId(), command.seatIds().size());

        // 비회원 생성
        Long guestId = guestPort.saveGuest(
                command.guestName(),
                command.guestPhone(),
                command.guestBirth(),
                passwordEncoder.encode(command.guestPassword())
        );

        // 예매 생성
        Reservation reservation = Reservation.createGuestReservation(guestId, command.screeningId(), screeningInfo.price(), command.seatIds());
        Reservation savedReservation = reservationPersistencePort.saveReservation(reservation);

        return buildReservationDetailResult(savedReservation, screeningInfo, seatInfoMap);
    }

    @Override
    @Transactional
    public void cancelReservation(Long userId, Long reservationId) {

        // 예매 조회
        Reservation reservation = reservationPersistencePort.findReservationWithSeatsById(reservationId)
                .orElseThrow(() -> new GeneralException(ReservationErrorCode.RESERVATION_NOT_FOUND));

        // 본인 예매인지 확인
        if (!userId.equals(reservation.getUserId())) {
            throw new GeneralException(ReservationErrorCode.RESERVATION_NOT_FOUND);
        }

        // 예매 취소
        reservation.cancel();

        // 좌석 수 복구
        screeningPort.increaseScreeningSeats(reservation.getScreeningId(), reservation.getSeatCount());

        // 예매 상태 업데이트
        reservationPersistencePort.updateReservationStatus(reservationId, reservation.getStatus());
    }


    @Override
    @Transactional
    public void cancelGuestReservation(CancelGuestReservationCommand command) {

        // 예매 조회
        Reservation reservation = reservationPersistencePort.findReservationWithSeatsById(command.reservationId())
                .orElseThrow(() -> new GeneralException(ReservationErrorCode.RESERVATION_NOT_FOUND));

        // 비회원 예매인지 확인
        if (!reservation.isGuest()) {
            throw new GeneralException(ReservationErrorCode.RESERVATION_NOT_FOUND);
        }

        // 비회원 정보 검증
        verifyGuestInfo(reservation.getGuestId(), command);

        // 예매 취소
        reservation.cancel();

        // 좌석 수 복구
        screeningPort.increaseScreeningSeats(reservation.getScreeningId(), reservation.getSeatCount());

        // 예매 상태 업데이트
        reservationPersistencePort.updateReservationStatus(reservation.getId(), reservation.getStatus());
    }


    // 상영 정보 조회
    private ScreeningInfoResult findScreeningInfoOrThrow(Long screeningId) {
        return screeningPort.findScreeningInfoById(screeningId)
                .orElseThrow(() -> new GeneralException(ReservationErrorCode.SCREENING_NOT_FOUND));
    }

    // 좌석 검증
    private Map<Long, SeatInfoResult> validateAndGetSeats(List<Long> seatIds, Long screeningId, Long hallTypeId) {

        // 좌석 조회
        Map<Long, SeatInfoResult> seatInfoMap = seatPort.findSeatInfoByIdsAndHallTypeId(seatIds, hallTypeId);
        if (seatInfoMap.size() != seatIds.size()) {
            throw new GeneralException(ReservationErrorCode.SEAT_NOT_FOUND);
        }

        // 이미 예약된 좌석인지 확인
        List<Long> reservedSeatIds = reservationPersistencePort.findReservedSeatIdsByScreeningId(screeningId);
        boolean reserved = seatIds.stream().anyMatch(reservedSeatIds::contains);
        if (reserved) {
            throw new GeneralException(ReservationErrorCode.SEAT_ALREADY_RESERVED);
        }

        return seatInfoMap;
    }

    // 비회원 정보 검증
    private void verifyGuestInfo(Long guestId, CancelGuestReservationCommand command) {

        GuestInfoResult guestInfo = guestPort.findGuestInfoById(guestId)
                .orElseThrow(() -> new GeneralException(ReservationErrorCode.GUEST_AUTH_FAILED));

        if (!guestInfo.phone().equals(command.guestPhone())
                || !guestInfo.birth().equals(command.guestBirth())
                || !passwordEncoder.matches(command.guestPassword(), guestInfo.password())) {
            throw new GeneralException(ReservationErrorCode.GUEST_AUTH_FAILED);
        }
    }

    private ReservationDetailResult buildReservationDetailResult(Reservation reservation, ScreeningInfoResult screeningInfo, Map<Long, SeatInfoResult> seatInfoMap) {

        List<SeatInfoResult> seats = reservation.getSeatIds().stream()
                .map(seatInfoMap::get)
                .toList();

        return new ReservationDetailResult(
                reservation.getId(),
                reservation.getReservationNumber(),
                reservation.getStatus(),
                screeningInfo.movieTitle(),
                screeningInfo.theaterName(),
                screeningInfo.hallName(),
                screeningInfo.startAt(),
                screeningInfo.endAt(),
                seats,
                reservation.getTotalPrice(),
                reservation.getCreatedAt()
        );
    }
}
