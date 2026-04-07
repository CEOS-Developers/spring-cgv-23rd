package com.ceos.spring_cgv_23rd.domain.reservation.service;

import com.ceos.spring_cgv_23rd.domain.guest.entity.Guest;
import com.ceos.spring_cgv_23rd.domain.guest.repository.GuestRepository;
import com.ceos.spring_cgv_23rd.domain.reservation.dto.ReservationRequestDTO;
import com.ceos.spring_cgv_23rd.domain.reservation.dto.ReservationResponseDTO;
import com.ceos.spring_cgv_23rd.domain.reservation.entity.Reservation;
import com.ceos.spring_cgv_23rd.domain.reservation.entity.ReservationSeat;
import com.ceos.spring_cgv_23rd.domain.reservation.enums.ReservationStatus;
import com.ceos.spring_cgv_23rd.domain.reservation.exception.ReservationErrorCode;
import com.ceos.spring_cgv_23rd.domain.reservation.repository.ReservationRepository;
import com.ceos.spring_cgv_23rd.domain.reservation.repository.ReservationSeatRepository;
import com.ceos.spring_cgv_23rd.domain.screening.entity.Screening;
import com.ceos.spring_cgv_23rd.domain.screening.exception.ScreeningErrorCode;
import com.ceos.spring_cgv_23rd.domain.screening.repository.ScreeningRepository;
import com.ceos.spring_cgv_23rd.domain.theater.adapter.out.persistence.entity.SeatEntity;
import com.ceos.spring_cgv_23rd.domain.theater.adapter.out.persistence.repository.SeatJpaRepository;
import com.ceos.spring_cgv_23rd.domain.user.adapter.out.persistence.entity.UserEntity;
import com.ceos.spring_cgv_23rd.domain.user.adapter.out.persistence.repository.UserJpaRepository;
import com.ceos.spring_cgv_23rd.domain.user.exception.UserErrorCode;
import com.ceos.spring_cgv_23rd.global.apiPayload.exception.GeneralException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReservationServiceImpl implements ReservationService {

    private final ReservationRepository reservationRepository;
    private final ReservationSeatRepository reservationSeatRepository;
    private final ScreeningRepository screeningRepository;
    private final UserJpaRepository userRepository;
    private final SeatJpaRepository seatRepository;
    private final GuestRepository guestRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public ReservationResponseDTO.ReservationDetailResponseDTO createReservation(Long userId, ReservationRequestDTO.CreateReservationRequestDTO request) {

        // 유저 조회
        UserEntity userEntity = userRepository.findById(userId)
                .orElseThrow(() -> new GeneralException(UserErrorCode.USER_NOT_FOUND));


        // 상영 스케줄 조회
        Screening screening = screeningRepository.findWithDetailsById(request.screeningId())
                .orElseThrow(() -> new GeneralException(ScreeningErrorCode.SCREENING_NOT_FOUND));

        // 좌석 조회
        List<SeatEntity> seats = validateAndGetSeats(request.seatIds(), request.screeningId());

        // 남은 좌석 차감
        screening.decreaseRemainingSeats(seats.size());

        // 예매 생성
        Reservation reservation = Reservation.createReservation(userEntity, screening, seats.size(), generateReservationNumber());

        reservationRepository.save(reservation);

        // 예매 좌석 생성
        List<ReservationSeat> reservationSeats = createReservationSeats(reservation, seats);

        return ReservationResponseDTO.ReservationDetailResponseDTO.of(reservation, reservationSeats);
    }

    @Override
    @Transactional
    public void cancelReservation(Long userId, Long reservationId) {

        // 유저 조회
        UserEntity userEntity = userRepository.findById(userId)
                .orElseThrow(() -> new GeneralException(UserErrorCode.USER_NOT_FOUND));


        // 예매 조회
        Reservation reservation = reservationRepository.findWithScreeningById(reservationId)
                .orElseThrow(() -> new GeneralException(ReservationErrorCode.RESERVATION_NOT_FOUND));

        // 본인 예약인지 확인
        if (!userEntity.equals(reservation.getUserEntity())) {
            throw new GeneralException(ReservationErrorCode.RESERVATION_NOT_FOUND);
        }

        // 예매 취소 및 예매 좌석 복구
        cancelReservationInternal(reservation);
    }

    @Override
    @Transactional
    public ReservationResponseDTO.ReservationDetailResponseDTO createGuestReservation(ReservationRequestDTO.CreateGuestReservationRequestDTO request) {

        // 상영 스케줄 조회
        Screening screening = screeningRepository.findWithDetailsById(request.screeningId())
                .orElseThrow(() -> new GeneralException(ScreeningErrorCode.SCREENING_NOT_FOUND));

        // 좌석 조회
        List<SeatEntity> seats = validateAndGetSeats(request.seatIds(), request.screeningId());

        // 남은 좌석 차감
        screening.decreaseRemainingSeats(seats.size());

        // 비회원 생성
        Guest guest = Guest.builder()
                .name(request.guestName())
                .phone(request.guestPhone())
                .birth(request.guestBirth())
                .password(passwordEncoder.encode(request.guestPassword()))
                .build();

        guestRepository.save(guest);

        // 예매 생성
        Reservation reservation = Reservation.createGuestReservation(guest, screening, seats.size(), generateReservationNumber());

        reservationRepository.save(reservation);

        // 예매 좌석 생성
        List<ReservationSeat> reservationSeats = createReservationSeats(reservation, seats);

        return ReservationResponseDTO.ReservationDetailResponseDTO.of(reservation, reservationSeats);
    }

    @Override
    @Transactional
    public void cancelGuestReservation(ReservationRequestDTO.CancelGuestReservationRequestDTO request) {

        // 예매 조회
        Reservation reservation = reservationRepository.findWithScreeningByReservationNumber(request.reservationNumber())
                .orElseThrow(() -> new GeneralException(ReservationErrorCode.RESERVATION_NOT_FOUND));

        // 비회원 예매인지 확인
        if (!reservation.isGuest()) {
            throw new GeneralException(ReservationErrorCode.RESERVATION_NOT_FOUND);
        }

        // 예매 정보가 맞는지 확인
        Guest guest = reservation.getGuest();
        if (!guest.getPhone().equals(request.guestPhone())
                || !guest.getBirth().equals(request.guestBirth())
                || !passwordEncoder.matches(request.guestPassword(), guest.getPassword())) {
            throw new GeneralException(ReservationErrorCode.GUEST_AUTH_FAILED);
        }

        // 예매 취소 및 예매 좌석 복구
        cancelReservationInternal(reservation);
    }

    private List<SeatEntity> validateAndGetSeats(List<Long> seatIds, Long screeningId) {

        // 좌석 조회
        List<SeatEntity> seats = seatRepository.findAllByIdIn(seatIds);
        if (seats.size() != seatIds.size()) {
            throw new GeneralException(ReservationErrorCode.SEAT_NOT_FOUND);
        }

        // 이미 예약된 좌석인지 확인
        List<Long> reservedSeatIds = reservationSeatRepository.findReservedSeatIdsByScreeningId(screeningId);
        boolean reserved = seatIds.stream().anyMatch(reservedSeatIds::contains);
        if (reserved) {
            throw new GeneralException(ReservationErrorCode.SEAT_ALREADY_RESERVED);
        }

        return seats;
    }

    private List<ReservationSeat> createReservationSeats(Reservation reservation, List<SeatEntity> seats) {

        // 예매 좌석 생성
        List<ReservationSeat> reservationSeats = seats.stream()
                .map(seat -> ReservationSeat.createReservationSeat(reservation, seat))
                .toList();
        reservationSeatRepository.saveAll(reservationSeats);

        return reservationSeats;
    }

    private void cancelReservationInternal(Reservation reservation) {

        // 이미 취소된 예약인지 확인
        if (reservation.getStatus() == ReservationStatus.CANCELLED) {
            throw new GeneralException(ReservationErrorCode.ALREADY_CANCELLED);
        }

        // 예매 좌석 수 조회
        List<ReservationSeat> reservationSeats = reservationSeatRepository.findByReservationId(reservation.getId());

        // 남은 좌석 수 복구
        reservation.getScreening().increaseRemainingSeats(reservationSeats.size());

        // 예매 취소
        reservation.cancel();
    }


    private String generateReservationNumber() {
        String datePart = LocalDate.now().format(DateTimeFormatter.ofPattern("yyMMdd"));
        String randomPart = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        return datePart + "-" + randomPart;
    }
}
