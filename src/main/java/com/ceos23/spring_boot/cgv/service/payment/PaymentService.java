package com.ceos23.spring_boot.cgv.service.payment;

import com.ceos23.spring_boot.cgv.domain.payment.PaymentLog;
import com.ceos23.spring_boot.cgv.global.exception.ConflictException;
import com.ceos23.spring_boot.cgv.global.exception.ErrorCode;
import com.ceos23.spring_boot.cgv.global.exception.NotFoundException;
import com.ceos23.spring_boot.cgv.global.logging.AuditLogService;
import com.ceos23.spring_boot.cgv.global.logging.BusinessMetricRecorder;
import com.ceos23.spring_boot.cgv.repository.payment.PaymentLogRepository;
import com.ceos23.spring_boot.cgv.repository.reservation.ReservationRepository;
import java.util.Map;
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
    private final AuditLogService auditLogService;
    private final BusinessMetricRecorder businessMetricRecorder;

    @Transactional
    public PaymentLog startPayment(PaymentCreateCommand command) {
        long startTime = System.currentTimeMillis();

        try {
            PaymentLog paymentLog = paymentLogRepository.saveAndFlush(new PaymentLog(
                    command.paymentId(),
                    command.orderName(),
                    command.amount(),
                    command.detail()
            ));
            auditLogService.info(
                    "payment_started",
                    Map.of(
                            "paymentId", paymentLog.getPaymentId(),
                            "amount", paymentLog.getAmount()
                    )
            );
            businessMetricRecorder.recordPaymentEvent("start", "success", System.currentTimeMillis() - startTime);
            return paymentLog;
        } catch (DataIntegrityViolationException exception) {
            ConflictException conflictException = new ConflictException(ErrorCode.DUPLICATE_PAYMENT_ID);
            auditLogService.warn(
                    "payment_start_failed",
                    Map.of("reason", conflictException.getErrorCode().getCode())
            );
            businessMetricRecorder.recordPaymentEvent("start", "failure", System.currentTimeMillis() - startTime);
            throw conflictException;
        }
    }

    @Transactional
    public PaymentLog completePayment(String paymentId) {
        long startTime = System.currentTimeMillis();

        try {
            PaymentLog paymentLog = findPaymentById(paymentId);
            paymentLog.complete();
            auditLogService.info("payment_completed", Map.of("paymentId", paymentId));
            businessMetricRecorder.recordPaymentEvent("complete", "success", System.currentTimeMillis() - startTime);
            return paymentLog;
        } catch (RuntimeException exception) {
            recordPaymentFailure("payment_complete_failed", "complete", paymentId, startTime, exception);
            throw exception;
        }
    }

    @Transactional
    public PaymentLog cancelPayment(String paymentId) {
        long startTime = System.currentTimeMillis();

        try {
            PaymentLog paymentLog = findPaymentById(paymentId);
            paymentLog.cancel();
            auditLogService.info("payment_cancelled", Map.of("paymentId", paymentId));
            businessMetricRecorder.recordPaymentEvent("cancel", "success", System.currentTimeMillis() - startTime);
            return paymentLog;
        } catch (RuntimeException exception) {
            recordPaymentFailure("payment_cancel_failed", "cancel", paymentId, startTime, exception);
            throw exception;
        }
    }

    @Transactional
    public void expirePayment(String paymentId) {
        long startTime = System.currentTimeMillis();

        try {
            findPaymentById(paymentId).expire();
            auditLogService.info("payment_expired", Map.of("paymentId", paymentId));
            businessMetricRecorder.recordPaymentEvent("expire", "success", System.currentTimeMillis() - startTime);
        } catch (RuntimeException exception) {
            recordPaymentFailure("payment_expire_failed", "expire", paymentId, startTime, exception);
            throw exception;
        }
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

    private void recordPaymentFailure(
            String eventName,
            String action,
            String paymentId,
            long startTime,
            RuntimeException exception
    ) {
        String errorCode = exception instanceof NotFoundException notFoundException
                ? notFoundException.getErrorCode().getCode()
                : exception instanceof ConflictException conflictException
                ? conflictException.getErrorCode().getCode()
                : ErrorCode.INTERNAL_SERVER_ERROR.getCode();

        auditLogService.warn(
                eventName,
                Map.of(
                        "paymentId", paymentId,
                        "reason", errorCode
                )
        );
        businessMetricRecorder.recordPaymentEvent(action, "failure", System.currentTimeMillis() - startTime);
    }
}
