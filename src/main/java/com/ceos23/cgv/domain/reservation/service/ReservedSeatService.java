package com.ceos23.cgv.domain.reservation.service;

import com.ceos23.cgv.domain.movie.entity.Screening;
import com.ceos23.cgv.domain.movie.repository.ScreeningRepository;
import com.ceos23.cgv.domain.reservation.dto.ReservedSeatRequest;
import com.ceos23.cgv.domain.reservation.entity.Reservation;
import com.ceos23.cgv.domain.reservation.entity.ReservedSeat;
import com.ceos23.cgv.domain.reservation.repository.ReservationRepository;
import com.ceos23.cgv.domain.reservation.repository.ReservedSeatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReservedSeatService {

    private final ReservedSeatRepository reservedSeatRepository;
    private final ReservationRepository reservationRepository;
    private final ScreeningRepository screeningRepository;

    @Transactional
    public List<ReservedSeat> createReservedSeats(ReservedSeatRequest request) {
        Reservation reservation = reservationRepository.findById(request.getReservationId())
                .orElseThrow(() -> new IllegalArgumentException("예매 정보를 찾을 수 없습니다."));
        Screening screening = screeningRepository.findById(request.getScreeningId())
                .orElseThrow(() -> new IllegalArgumentException("상영 일정을 찾을 수 없습니다."));

        // 요청받은 좌석들 리스트업 (엔티티의 row, col 매핑)
        List<ReservedSeat> reservedSeats = request.getSeats().stream()
                .map(seatInfo -> ReservedSeat.builder()
                        .reservation(reservation)
                        .screening(screening)
                        .seatRow(seatInfo.getRow())
                        .seatCol(seatInfo.getCol())
                        .build())
                .collect(Collectors.toList());

        try {
            // 이미 있는 자리면 여기서 예외가 터짐
            return reservedSeatRepository.saveAll(reservedSeats);
        } catch (DataIntegrityViolationException e) {
            // 예외를 잡아채서 클라이언트가 알기 쉬운 에러로 변환
            throw new IllegalStateException("이미 예매가 완료된 좌석이 포함되어 있습니다. 다른 좌석을 선택해 주세요.");
        }
    }

    public List<ReservedSeat> getReservedSeatsByScreeningId(Long screeningId) {
        return reservedSeatRepository.findByScreeningId(screeningId);
    }
}