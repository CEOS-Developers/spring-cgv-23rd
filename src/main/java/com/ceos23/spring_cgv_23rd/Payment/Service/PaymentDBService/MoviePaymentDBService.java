package com.ceos23.spring_cgv_23rd.Payment.Service.PaymentDBService;

import com.ceos23.spring_cgv_23rd.Payment.DTO.PaymentRequestDTO;
import com.ceos23.spring_cgv_23rd.Payment.Domain.PayType;
import com.ceos23.spring_cgv_23rd.Payment.Domain.Payment;
import com.ceos23.spring_cgv_23rd.Payment.Repository.PaymentRepository;
import com.ceos23.spring_cgv_23rd.Payment.Service.PaymentFacadeService.PaymentIdHandler;
import com.ceos23.spring_cgv_23rd.Reservation.Domain.Reservation;
import com.ceos23.spring_cgv_23rd.Reservation.Domain.ReservationStatus;
import com.ceos23.spring_cgv_23rd.Reservation.Repository.ReservationRepository;
import com.ceos23.spring_cgv_23rd.global.Exception.CustomException;
import com.ceos23.spring_cgv_23rd.global.Exception.ErrorCode;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class MoviePaymentDBService {

    private final ReservationRepository reservationRepository;
    private final PaymentIdHandler paymentIdHandler;
    private final PaymentRepository paymentRepository;

    public MoviePaymentDBService(ReservationRepository reservationRepository, PaymentIdHandler paymentIdHandler, PaymentRepository paymentRepository) {
        this.reservationRepository = reservationRepository;
        this.paymentIdHandler = paymentIdHandler;
        this.paymentRepository = paymentRepository;
    }

    private Reservation getActiveReservationById(long targetId){
        return reservationRepository.findActivatedReservationById(targetId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "존재하지 않는 예약입니다.")
        );
    }

    @Transactional
    public Payment setPayment(long targetId,
                              PaymentRequestDTO req){
        Reservation reservation = getActiveReservationById(targetId);
        String paymentId = paymentIdHandler.getPaymentId();

        if(!reservation.canPay()){
            throw new CustomException(ErrorCode.RESERVATION_IS_UNAVAILABLE);
        }
        reservation.buyReservation();

        Payment payment = Payment.create(paymentId, req.storeId(), req.orderName(), req.totalPayAmount(), req.currency(), PayType.RESERVATION, reservation.getId());
        return paymentRepository.save(payment);
    }

    @Transactional
    public void failPayment(long id){
        Payment payment = paymentRepository.findById(id).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND_PAYMENT)
        );

        payment.paymentFail();
    }

    @Transactional
    public Payment successPayment(long id){
        Payment payment = paymentRepository.findById(id).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND_PAYMENT)
        );
        Reservation reservation = reservationRepository.findById(payment.getTargetId()).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND_RESERVATION)
        );

        payment.paymentSuccess();
        reservation.buyReservation();

        return payment;
    }

    @Transactional
    public void cancelPayment(long id){
        Payment payment = paymentRepository.findById(id).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND_PAYMENT)
        );
        Reservation reservation = reservationRepository.findById(payment.getTargetId()).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND_RESERVATION)
        );

        payment.cancel();
        reservation.cancel();
    }
}
