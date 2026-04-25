package com.ceos23.spring_boot.cgv.service.payment;

import com.ceos23.spring_boot.cgv.domain.payment.PaymentLog;
import com.ceos23.spring_boot.cgv.global.exception.ConflictException;
import com.ceos23.spring_boot.cgv.global.exception.ErrorCode;
import com.ceos23.spring_boot.cgv.global.exception.NotFoundException;
import com.ceos23.spring_boot.cgv.repository.payment.PaymentLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PaymentService {

    private final PaymentLogRepository paymentLogRepository;

    @Transactional
    public PaymentLog requestPayment(PaymentCreateCommand command) {
        if (paymentLogRepository.existsByPaymentId(command.paymentId())) {
            throw new ConflictException(ErrorCode.DUPLICATE_PAYMENT_ID);
        }

        return paymentLogRepository.save(new PaymentLog(
                command.paymentId(),
                command.orderName(),
                command.amount(),
                command.detail()
        ));
    }

    @Transactional
    public PaymentLog cancelPayment(String paymentId) {
        PaymentLog paymentLog = getPayment(paymentId);
        paymentLog.cancel();
        return paymentLog;
    }

    public PaymentLog getPayment(String paymentId) {
        return paymentLogRepository.findByPaymentId(paymentId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.PAYMENT_NOT_FOUND));
    }
}
