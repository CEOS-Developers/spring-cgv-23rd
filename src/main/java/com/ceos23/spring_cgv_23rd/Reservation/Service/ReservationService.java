package com.ceos23.spring_cgv_23rd.Reservation.Service;

import com.ceos23.spring_cgv_23rd.DiscountPolicy.*;
import com.ceos23.spring_cgv_23rd.Reservation.DTO.Request.ReservationRequestDTO;
import com.ceos23.spring_cgv_23rd.Reservation.DTO.Request.ReservationSeatInfo;
import com.ceos23.spring_cgv_23rd.Reservation.DTO.Request.ReservationType;
import com.ceos23.spring_cgv_23rd.Reservation.DTO.Request.WithdrawReservationDTO;
import com.ceos23.spring_cgv_23rd.Reservation.DTO.Response.RemainingSeatsDTO;
import com.ceos23.spring_cgv_23rd.Reservation.DTO.Response.ReservationResponseDTO;
import com.ceos23.spring_cgv_23rd.Reservation.Domain.Reservation;
import com.ceos23.spring_cgv_23rd.Reservation.Domain.ReservationSeat;
import com.ceos23.spring_cgv_23rd.Reservation.Domain.SeatInfo;
import com.ceos23.spring_cgv_23rd.Reservation.Repository.ReservationRepository;
import com.ceos23.spring_cgv_23rd.Reservation.Repository.ReservationSeatRepository;
import com.ceos23.spring_cgv_23rd.Screen.Domain.Screening;
import com.ceos23.spring_cgv_23rd.Screen.Repository.ScreeningRepository;
import com.ceos23.spring_cgv_23rd.Screen.Service.SeatValidator;
import com.ceos23.spring_cgv_23rd.User.Domain.User;
import com.ceos23.spring_cgv_23rd.User.Repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.boot.web.server.Http2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ReservationService {
    private final UserRepository userRepository;
    private final ScreeningRepository screeningRepository;
    private final ReservationRepository reservationRepository;
    private final ReservationSeatRepository reservationSeatRepository;
    private final SeatValidator seatValidator;
    private final DiscountPolicyFactory discountPolicyFactory;

    ReservationService(UserRepository userRepository,
                       ScreeningRepository screeningRepository,
                       ReservationRepository reservationRepository,
                       ReservationSeatRepository reservationSeatRepository,
                       SeatValidator seatValidator,
                       DiscountPolicyFactory discountPolicyFactory){
        this.userRepository = userRepository;
        this.screeningRepository = screeningRepository;
        this.reservationRepository = reservationRepository;
        this.reservationSeatRepository = reservationSeatRepository;
        this.seatValidator = seatValidator;
        this.discountPolicyFactory = discountPolicyFactory;
    }

    /**
     * TODO: Authentication과 USER 정보 가져오는 것 연결하기
     * TODO: 자리 없으면 예매 불가능하게하기
     *
     * @param req
     * @return
     */

    public ReservationResponseDTO reserve(ReservationRequestDTO req){
        User user = userRepository.findById(req.userId()).orElseThrow(() -> new EntityNotFoundException("유저 정보가 없습니다."));
        Screening screening = screeningRepository.findById(req.screeningId()).orElseThrow(() -> new EntityNotFoundException("상영 정보가 없습니다."));

        seatValidator.checkValidity(screening, req.seatInfos());

        Reservation reservation = Reservation.create(
                user,
                screening,
                req.toReservingSeats(),
                discountPolicyFactory.create(screening, req.toSeatInfos())
        );

        reservationRepository.save(reservation);
        return ReservationResponseDTO.createForReserve(reservation);
    }

    /**
     * TODO: Authentication과 USER 정보 가져오는 것 연결하기
     * screening 객체를 전해주면 남은 좌석 정보 전달
     *
     */
    public ResponseEntity<RemainingSeatsDTO> getSeats(long screeningId){
        Screening screening = screeningRepository.findById(screeningId).orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "해당 상영정보가 존재하지 않습니다."));

        List<String> reservedSeats = reservationSeatRepository.findByScreening(screening).stream()
                .map(ReservationSeat::getSeatName)
                .toList();

        return ResponseEntity.ok(
                RemainingSeatsDTO.create(screeningId, reservedSeats)
        );
    }

    public void cancel(long reservationId) throws Exception{
        screeningRepository.deleteById(reservationId);
    }
}
