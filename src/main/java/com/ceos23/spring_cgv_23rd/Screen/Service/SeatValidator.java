package com.ceos23.spring_cgv_23rd.Screen.Service;

import com.ceos23.spring_cgv_23rd.Reservation.DTO.Request.ReservationSeatInfo;
import com.ceos23.spring_cgv_23rd.Reservation.Domain.ReservationSeat;
import com.ceos23.spring_cgv_23rd.Reservation.Repository.ReservationSeatRepository;
import com.ceos23.spring_cgv_23rd.Screen.Domain.Screening;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class SeatValidator {
    ReservationSeatRepository reservationSeatRepository;

    public SeatValidator(ReservationSeatRepository reservationSeatRepository){
        this.reservationSeatRepository = reservationSeatRepository;
    }

    public void checkValidity(Screening screening, List<ReservationSeatInfo> infos){
        List<ReservationSeat> rs = reservationSeatRepository.findByScreening(screening);

        Set<String> reservedSeatNames = rs.stream()
                .map(ReservationSeat::getSeatName)
                .collect(Collectors.toSet());

        Set<String> requestedSeatNames = new HashSet<>();

        for (ReservationSeatInfo rsi : infos){
            if (!requestedSeatNames.add(rsi.seatName())){
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "좌석을 중복선택할 수 없습니다.");
            }

            if (reservedSeatNames.contains(rsi.seatName())){
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "이미 선택된 좌석입니다.");
            }
        }
    }
}
