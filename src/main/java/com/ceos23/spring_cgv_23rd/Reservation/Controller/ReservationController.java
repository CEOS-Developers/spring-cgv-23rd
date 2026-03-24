package com.ceos23.spring_cgv_23rd.Reservation.Controller;

import com.ceos23.spring_cgv_23rd.Reservation.DTO.Request.ReservationRequestDTO;
import com.ceos23.spring_cgv_23rd.Reservation.DTO.Request.WithdrawReservationDTO;
import com.ceos23.spring_cgv_23rd.Reservation.DTO.Response.ReservationResponseDTO;
import com.ceos23.spring_cgv_23rd.Reservation.Service.ReservationService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/reservation")
@Controller
public class ReservationController {
    ReservationService reservationService;

    public ReservationController(ReservationService reservationService){
        this.reservationService = reservationService;
    }

    @PostMapping
    public ResponseEntity<ReservationResponseDTO> reserve(
            @RequestBody ReservationRequestDTO requestDTO
            ){
        return reservationService.reserve(requestDTO);
    }

    @DeleteMapping("/{reservationId}")
    public ResponseEntity<ReservationResponseDTO> withdraw(
            @PathVariable long reservationId
    ) {
        WithdrawReservationDTO requestDTO = WithdrawReservationDTO.builder()
                .reservationId(reservationId)
                .build();

        return reservationService.withdraw(requestDTO);
    }
}
