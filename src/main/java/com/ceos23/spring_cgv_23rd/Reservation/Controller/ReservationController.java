package com.ceos23.spring_cgv_23rd.Reservation.Controller;

import com.ceos23.spring_cgv_23rd.Reservation.DTO.Request.ReservationRequestDTO;
import com.ceos23.spring_cgv_23rd.Reservation.DTO.Request.WithdrawReservationDTO;
import com.ceos23.spring_cgv_23rd.Reservation.DTO.Response.RemainingSeatsDTO;
import com.ceos23.spring_cgv_23rd.Reservation.DTO.Response.ReservationResponseDTO;
import com.ceos23.spring_cgv_23rd.Reservation.Service.ReservationService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/reservation")
@RestController
public class ReservationController {
    ReservationService reservationService;

    public ReservationController(ReservationService reservationService){
        this.reservationService = reservationService;
    }

    @PostMapping("/seats")
    public ResponseEntity<ReservationResponseDTO> selectSeats(
            @RequestBody ReservationRequestDTO requestDTO,
            @AuthenticationPrincipal User user
            ){
        return ResponseEntity.ok(reservationService.reserve(user.getUsername(), requestDTO));
    }

    @PostMapping(params = "reservationId")
    public ResponseEntity<ReservationResponseDTO> reserve(
            @RequestParam long reservationId
    ){
        return ResponseEntity.ok(reservationService.reserve(reservationId));
    }

    /**
     * TODO: Authentication과 USER 정보 가져오는 것 연결하기
     * screening 객체를 전해주면 남은 좌석 정보 전달
     *
     */
    @GetMapping("/screeningId")
    public ResponseEntity<RemainingSeatsDTO> getRemaining(
            @RequestParam long screeningId
    ){
        return reservationService.getSeats(screeningId);
    }


    @DeleteMapping("/{reservationId}")
    public ResponseEntity<ReservationResponseDTO> withdraw(
            @AuthenticationPrincipal User user,
            @PathVariable long reservationId
    ) throws Exception {
        reservationService.cancel(user.getUsername(), reservationId);
        ReservationResponseDTO res = ReservationResponseDTO.createForDelete();

        return ResponseEntity.ok(res);
    }
}
