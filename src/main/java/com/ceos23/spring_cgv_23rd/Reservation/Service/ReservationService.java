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
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

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
     * 예매정보 저장하기, 아직 미결제
     *
     * @param req 요청사항
     * @return 완료된 예매정보
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

            return ReservationResponseDTO.createForReserve(user, reservation);
        } catch (DataIntegrityViolationException de){
            //잘못된 요청 이외의 동시성 처리
            //UK를 통해서 동시에 같은 좌석 예매요청에 대해서 처리한다.
            de.printStackTrace();
            reservation.cancel();
            throw new CustomException(ErrorCode.ALREADY_OCCUPIED);
        }
    }

    /**
     * 최종예매
     *
     * @param reservationId 예약ID
     * @return 최종결제정보
     */
    public ReservationResponseDTO reserve(long reservationId){
        Reservation reservation = reservationRepository.findById(reservationId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "예매정보가 존재하지 않습니다.")
        );

        paymentService.buy(
                PaymentRequestDTO.create(reservation, storeId, "영화예매"),
                reservation.getId()
        );

        return ReservationResponseDTO.createForPayment(reservation.getUser(), reservation);
    }

    /**
     * TODO: Authentication과 USER 정보 가져오는 것 연결하기
     * screening 객체를 전해주면 예약된 좌석 정보 전달
     *
     */
    public ResponseEntity<RemainingSeatsDTO> getSeats(long screeningId){
        Screening screening = screeningRepository.findById(screeningId).orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "해당 상영정보가 존재하지 않습니다."));

        List<String> reservedSeats = reservationSeatRepository.findByScreeningAndStatus(screening, ReservationStatus.RESERVED).stream()
                .filter(r -> r.getReservationStatus().equals(ReservationStatus.RESERVED))
                .map(ReservationSeat::getSeatName)
                .toList();

        return ResponseEntity.ok(
                RemainingSeatsDTO.create(screeningId, reservedSeats)
        );
    }

    public void cancel(String loginId, long reservationId){
        Reservation reservation = reservationRepository.findById(reservationId).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND_RESERVATION)
        );

        if (!(reservation.getUser().getLoginId().equals(loginId))) {
            throw new CustomException(ErrorCode.DIFFERENT_USER);
        }

        paymentService.cancel(reservation.getId());
        reservation.cancel();
    }
}
