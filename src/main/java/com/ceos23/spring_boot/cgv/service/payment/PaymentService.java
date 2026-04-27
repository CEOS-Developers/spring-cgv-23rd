package com.ceos23.spring_boot.cgv.service.payment;

import com.ceos23.spring_boot.cgv.domain.payment.PaymentLog;
import com.ceos23.spring_boot.cgv.global.exception.ConflictException;
import com.ceos23.spring_boot.cgv.global.exception.ErrorCode;
import com.ceos23.spring_boot.cgv.global.exception.NotFoundException;
import com.ceos23.spring_boot.cgv.repository.payment.PaymentLogRepository;
import com.ceos23.spring_boot.cgv.repository.reservation.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PaymentService {

    private final PaymentLogRepository paymentLogRepository;
    private final ReservationRepository reservationRepository;

    @Transactional
    public PaymentLog startPayment(PaymentCreateCommand command) {
        try {
            return paymentLogRepository.saveAndFlush(new PaymentLog(
                    command.paymentId(),
                    command.orderName(),
                    command.amount(),
                    command.detail()
            ));
        } catch (DataIntegrityViolationException exception) {
            throw new ConflictException(ErrorCode.DUPLICATE_PAYMENT_ID);
        }
    }

    @Transactional
    public PaymentLog completePayment(String paymentId) {
        PaymentLog paymentLog = findPaymentById(paymentId);
        paymentLog.complete();
        return paymentLog;
    }

    @Transactional
    public PaymentLog cancelPayment(String paymentId) {
        PaymentLog paymentLog = findPaymentById(paymentId);
        paymentLog.cancel();
        return paymentLog;
    }

    @Transactional
    public void expirePayment(String paymentId) {
        findPaymentById(paymentId).expire();
    }

    public PaymentLog getPayment(String paymentId, Long userId) {
        reservationRepository.findByPaymentIdAndUserId(paymentId, userId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.PAYMENT_NOT_FOUND));

        return findPaymentById(paymentId);
    }

    private PaymentLog findPaymentById(String paymentId) {
        return paymentLogRepository.findByPaymentId(paymentId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.PAYMENT_NOT_FOUND));
    }
}
