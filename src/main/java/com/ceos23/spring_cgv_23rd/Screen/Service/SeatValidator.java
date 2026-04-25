package com.ceos23.spring_cgv_23rd.Screen.Service;

import com.ceos23.spring_cgv_23rd.Reservation.DTO.Request.ReservationSeatInfo;
import com.ceos23.spring_cgv_23rd.Reservation.Domain.ReservationSeat;
import com.ceos23.spring_cgv_23rd.Reservation.Repository.ReservationSeatRepository;
import com.ceos23.spring_cgv_23rd.Screen.Domain.Screening;
import com.ceos23.spring_cgv_23rd.global.Exception.CustomException;
import com.ceos23.spring_cgv_23rd.global.Exception.ErrorCode;
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
                throw new CustomException(ErrorCode.DIFFERENT_USER);
            }

            if (reservedSeatNames.contains(rsi.seatName())){
                throw new CustomException(ErrorCode.ALREADY_OCCUPIED);
            }
        }
    }
}
