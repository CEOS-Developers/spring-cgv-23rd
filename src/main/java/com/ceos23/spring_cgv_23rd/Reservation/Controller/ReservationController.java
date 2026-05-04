package com.ceos23.spring_cgv_23rd.Reservation.Controller;

import com.ceos23.spring_cgv_23rd.Reservation.DTO.Request.ReservationRequestDTO;
import com.ceos23.spring_cgv_23rd.Reservation.DTO.Response.RemainingSeatsDTO;
import com.ceos23.spring_cgv_23rd.Reservation.DTO.Response.ReservationResponseDTO;
import com.ceos23.spring_cgv_23rd.Reservation.Service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/reservation")
public class ReservationController {
    private final ReservationService reservationService;

    /**
     * 좌석 선택
     * 예매는 좌석 선택 -> Reservation 객체 형성 -> ReservationId 기반 결제로 이어집니다.
     * 이 메서드는 "좌석 선택" 기능입니다.
     *
     * @param requestDTO 요청정보. ScreeningId와 SeatInfo를 List 형태로 전해줍니다.
     * @param user 사용자 정보. 쿠키에서 자동으로 정보를 가져옵니다.
     * @return 좌석선택 후 생성된 Reservation 객체의 정보가 반환됩니다.
     */
    @PostMapping("/seats")
    public ResponseEntity<ReservationResponseDTO> selectSeats(
            @RequestBody ReservationRequestDTO requestDTO,
            @AuthenticationPrincipal User user
            ){
        return ResponseEntity.ok(reservationService.reserve(user.getUsername(), requestDTO));
    }

    /**
     * 좌석 선택
     * 예매는 좌석 선택 -> Reservation 객체 형성 -> ReservationId 기반 결제로 이어집니다.
     * 이 메서드는 "ReservationId 기반 결제" 기능입니다.
     *
     * @param reservationId 결제할 예약의 ID값입니다.
     * @param user 사용자 정보. 쿠키에서 자동으로 정보를 가져옵니다.
     * @return 좌석선택 후 생성된 Reservation 객체의 정보가 반환됩니다.
     */
    @PostMapping(params = "reservationId")
    public ResponseEntity<ReservationResponseDTO> reserve(
            @RequestParam long reservationId,
            @AuthenticationPrincipal User user
    ){
        return ResponseEntity.ok(reservationService.pay(user.getUsername(), reservationId));
    }

    /**
     * 좌석의 남은 정보를 가져옵니다.
     * 예매에 성공한 좌석의 이름들, 상영정보id, 총 예매가능좌석수, 현재 예매가능 좌석수를 가져옵니다.
     *
     * @param screeningId 가져올 상영정보의 id
     * @return 남은 좌석정보
     */
    @GetMapping(params = "screeningId")
    public ResponseEntity<RemainingSeatsDTO> getRemaining(
            @RequestParam long screeningId
    ){
        return reservationService.getSeats(screeningId);
    }

    /**
     * 이미 결제된 예약을 취소합니다.
     *
     * @param user 사용자 정보. 쿠키에서 자동으로 정보를 가져옵니다.
     * @param reservationId 취소할 예약id입니다.
     * @return 취소된 예약의 정보를 반환합니다.
     */
    @DeleteMapping("/{reservationId}")
    public ResponseEntity<ReservationResponseDTO> withdraw(
            @AuthenticationPrincipal User user,
            @PathVariable long reservationId
    ) {
        reservationService.cancel(user.getUsername(), reservationId);
        ReservationResponseDTO res = ReservationResponseDTO.createForDelete();

        return ResponseEntity.ok(res);
    }
}
