package com.ceos23.spring_cgv_23rd.Reservation.Service;

import com.ceos23.spring_cgv_23rd.Reservation.DTO.Request.ReservationRequestDTO;
import com.ceos23.spring_cgv_23rd.Reservation.DTO.Request.ReservationSeatInfo;
import com.ceos23.spring_cgv_23rd.Reservation.DTO.Request.ReservationType;
import com.ceos23.spring_cgv_23rd.Reservation.DTO.Request.WithdrawReservationDTO;
import com.ceos23.spring_cgv_23rd.Reservation.DTO.Response.ReservationResponseDTO;
import com.ceos23.spring_cgv_23rd.Reservation.Domain.Reservation;
import com.ceos23.spring_cgv_23rd.Reservation.Domain.ReservationSeat;
import com.ceos23.spring_cgv_23rd.Reservation.Repository.ReservationRepository;
import com.ceos23.spring_cgv_23rd.Screen.Domain.Screening;
import com.ceos23.spring_cgv_23rd.Screen.Repository.ScreeningRepository;
import com.ceos23.spring_cgv_23rd.User.Domain.User;
import com.ceos23.spring_cgv_23rd.User.Repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ReservationService {
    UserRepository userRepository;
    ScreeningRepository screeningRepository;
    ReservationRepository reservationRepository;

    ReservationService(UserRepository userRepository,
                       ScreeningRepository screeningRepository,
                       ReservationRepository reservationRepository){
        this.userRepository = userRepository;
        this.screeningRepository = screeningRepository;
        this.reservationRepository = reservationRepository;
    }

    @Transactional
    public ResponseEntity<ReservationResponseDTO> reserve(ReservationRequestDTO requestDTO){
        User user = userRepository.findById(requestDTO.userId()).orElseThrow(() -> new NullPointerException("유저정보없음"));
        Screening screening = screeningRepository.findById(requestDTO.screeningId()).orElseThrow(() -> new NullPointerException("상영정보없음"));

        Reservation reservation = Reservation.builder()
                .user(user)
                .screening(screening)
                .reservationDate(requestDTO.reservationDate())
                .totalPrice(requestDTO.totalPrice())
                .build();

        for (ReservationSeatInfo info : requestDTO.seatInfos()){
            ReservationSeat seat = ReservationSeat.builder()
                    .reservation(reservation).seatName(info.seatName()).seatInfo(info.info())
                    .build();

            reservation.getReservationSeats().add(seat);
        }

        Reservation re =  reservationRepository.save(reservation);

        ReservationResponseDTO response = ReservationResponseDTO.builder()
                .id(re.getId())
                .build();

        return ResponseEntity.ok(response);
    }

    @Transactional
    public ResponseEntity<ReservationResponseDTO> withdraw(WithdrawReservationDTO requestDTO){
        Reservation reservation = reservationRepository.findById(requestDTO.reservationId()).orElseThrow(
                () -> new RuntimeException("예약이 존재하지 않습니다.")
        );

        reservationRepository.delete(reservation);

        ReservationResponseDTO response = ReservationResponseDTO.builder()
                .id(reservation.getId())
                .build();

        return ResponseEntity.ok(response);
    }
}
