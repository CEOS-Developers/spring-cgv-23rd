package com.ceos23.spring_cgv_23rd.Reservation.Service;

import com.ceos23.spring_cgv_23rd.Payment.DTO.PaymentRequestDTO;
import com.ceos23.spring_cgv_23rd.Payment.Service.PaymentFacadeService.CancelablePaymentFacadeService;
import com.ceos23.spring_cgv_23rd.Reservation.DTO.Request.ReservationRequestDTO;
import com.ceos23.spring_cgv_23rd.Reservation.DTO.Response.RemainingSeatsDTO;
import com.ceos23.spring_cgv_23rd.Reservation.DTO.Response.ReservationResponseDTO;
import com.ceos23.spring_cgv_23rd.Reservation.Domain.Reservation;
import com.ceos23.spring_cgv_23rd.Reservation.Domain.ReservationSeat;
import com.ceos23.spring_cgv_23rd.Reservation.Domain.ReservationStatus;
import com.ceos23.spring_cgv_23rd.Reservation.Repository.ReservationRepository;
import com.ceos23.spring_cgv_23rd.Reservation.Repository.ReservationSeatRepository;
import com.ceos23.spring_cgv_23rd.Screen.Domain.Screening;
import com.ceos23.spring_cgv_23rd.Screen.Repository.ScreeningRepository;
import com.ceos23.spring_cgv_23rd.Screen.Service.SeatValidator;
import com.ceos23.spring_cgv_23rd.User.Domain.User;
import com.ceos23.spring_cgv_23rd.User.Repository.UserRepository;
import com.ceos23.spring_cgv_23rd.global.DiscountPolicy.DiscountPolicy;
import com.ceos23.spring_cgv_23rd.global.DiscountPolicy.DiscountPolicyFactory;
import com.ceos23.spring_cgv_23rd.global.Exception.CustomException;
import com.ceos23.spring_cgv_23rd.global.Exception.ErrorCode;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ReservationService {
    private final UserRepository userRepository;
    private final ScreeningRepository screeningRepository;
    private final ReservationRepository reservationRepository;
    private final ReservationSeatRepository reservationSeatRepository;
    private final SeatValidator seatValidator;
    private final DiscountPolicyFactory discountPolicyFactory;
    private final CancelablePaymentFacadeService paymentService;

    @Value("${concurrency.storeId}")
    private String storeId;

    ReservationService(UserRepository userRepository,
                       ScreeningRepository screeningRepository,
                       ReservationRepository reservationRepository,
                       ReservationSeatRepository reservationSeatRepository,
                       SeatValidator seatValidator,
                       DiscountPolicyFactory discountPolicyFactory,
                       @Qualifier("moviePaymentFacadeService") CancelablePaymentFacadeService paymentService){
        this.userRepository = userRepository;
        this.screeningRepository = screeningRepository;
        this.reservationRepository = reservationRepository;
        this.reservationSeatRepository = reservationSeatRepository;
        this.seatValidator = seatValidator;
        this.discountPolicyFactory = discountPolicyFactory;
        this.paymentService = paymentService;
    }

    /**
     * 좌석 선택
     * 예매는 좌석 선택 -> Reservation 객체 형성 -> ReservationId 기반 결제로 이어집니다.
     * 이 메서드는 "좌석 선택" 기능입니다.
     *
     * @param loginId 사용자 정보. 쿠키에서 자동으로 정보를 가져옵니다.
     * @param req 요청정보. ScreeningId와 SeatInfo를 List 형태로 전해줍니다.
     * @return 좌석선택 후 생성된 Reservation 객체의 정보가 반환됩니다.
     */
    @Transactional
    public ReservationResponseDTO reserve(String loginId, ReservationRequestDTO req){
        User user = userRepository.findByLoginId(loginId).orElseThrow(() -> new EntityNotFoundException("유저 정보가 없습니다."));
        Screening screening = screeningRepository.findById(req.screeningId()).orElseThrow(() -> new EntityNotFoundException("상영 정보가 없습니다."));
        DiscountPolicy discountPolicy = discountPolicyFactory.create(screening, req.toSeatInfos());

        seatValidator.checkValidity(screening, req.seatInfos());

        Reservation reservation = screening.reserve(
                user, req, discountPolicy
        );


        try{
            reservationRepository.save(reservation);
            reservationRepository.flush();

            String seatInfo = req.seatInfos().stream()
                    .map(r -> "좌석명: " + r.seatName() + ", 좌석정보: " + r.info().getInfo())
                    .collect(Collectors.joining(", "));
            log.info("좌석 예약됨: reservationId={}, screeningId={}, screeningInfo={}",
                    reservation.getId(), screening.getId(), seatInfo);

            return ReservationResponseDTO.createForReserve(user, reservation);
        } catch (DataIntegrityViolationException de){
            log.warn("이미 선택된 좌석 발생: reservationId={}, screeningId={}",
                    reservation.getId(), screening.getId());
            reservation.cancel();
            throw new CustomException(ErrorCode.ALREADY_OCCUPIED);
        }
    }

    /**
     * 좌석 선택
     * 예매는 좌석 선택 -> Reservation 객체 형성 -> ReservationId 기반 결제로 이어집니다.
     * 이 메서드는 "ReservationId 기반 결제" 기능입니다.
     *
     * @param reservationId 결제할 예약의 ID값입니다.
     * @param userLoginId 사용자 정보. 쿠키에서 자동으로 정보를 가져옵니다.
     * @return 좌석선택 후 생성된 Reservation 객체의 정보가 반환됩니다.
     */
    public ReservationResponseDTO pay(String userLoginId, long reservationId){
        Reservation reservation = reservationRepository.findById(reservationId).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND_RESERVATION)
        );

        User user = userRepository.findByLoginId(userLoginId).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND_USER)
        );

        if (!reservation.matchUserId(user.getLoginId())){
            throw new CustomException(ErrorCode.USER_NOT_MATCH);
        }

        paymentService.buy(
                PaymentRequestDTO.create(reservation, storeId, "영화예매"),
                reservation.getId(),
                user.getLoginId()
        );

        return ReservationResponseDTO.createForPayment(reservation.getUser(), reservation);
    }

    /**
     * 좌석의 남은 정보를 가져옵니다.
     *
     * @param screeningId 가져올 상영정보의 id
     * @return 남은 좌석정보
     */
    public ResponseEntity<RemainingSeatsDTO> getSeats(long screeningId){
        Screening screening = screeningRepository.findById(screeningId).orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "해당 상영정보가 존재하지 않습니다."));

        List<String> reservedSeats = reservationSeatRepository.findByScreeningAndStatus(screening, ReservationStatus.RESERVED).stream()
                .filter(r -> r.getReservationStatus().equals(ReservationStatus.RESERVED))
                .map(ReservationSeat::getSeatName)
                .toList();

        return ResponseEntity.ok(
                RemainingSeatsDTO.create(screening, reservedSeats)
        );
    }

    public void cancel(String loginId, long reservationId){
        try {
            Reservation reservation = reservationRepository.findById(reservationId).orElseThrow(
                    () -> new CustomException(ErrorCode.NOT_FOUND_RESERVATION)
            );

            if (!(reservation.getUser().getLoginId().equals(loginId))) {
                throw new CustomException(ErrorCode.DIFFERENT_USER);
            }

            paymentService.cancel(reservation.getId());
            reservation.cancel();
            log.info("결제가 취소됨. reservationId: {}", reservationId);
        } catch (CustomException ce){
            log.error("취소에 실패함. reservationId: {}", reservationId);
            throw ce;
        }
    }
}
