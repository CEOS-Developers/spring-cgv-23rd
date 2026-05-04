package com.ceos.spring_cgv_23rd.domain.payment.domain;

import com.ceos.spring_cgv_23rd.domain.payment.exception.PaymentErrorCode;
import com.ceos.spring_cgv_23rd.global.apiPayload.exception.GeneralException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class Payment {

    private Long id;
    private String paymentId;
    private PaymentStatus status;
    private String orderName;
    private Integer amount;
    private String pgProvider;
    private LocalDateTime paidAt;
    private LocalDateTime cancelledAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;


    public static Payment createReadyPayment(String paymentId, String orderName, int amount) {
        return Payment.builder()
                .paymentId(paymentId)
                .status(PaymentStatus.READY)
                .orderName(orderName)
                .amount(amount)
                .build();
    }

    public void markPaid(String pgProvider, LocalDateTime paidAt) {
        validateStatus(PaymentStatus.READY);

        this.status = PaymentStatus.PAID;
        this.pgProvider = pgProvider;
        this.paidAt = paidAt;
    }

    public void markFailed() {
        validateStatus(PaymentStatus.READY);

        this.status = PaymentStatus.FAILED;
    }

    public void markCancelled() {
        validateStatus(PaymentStatus.PAID);

        this.status = PaymentStatus.CANCELLED;
        this.cancelledAt = LocalDateTime.now();
    }

    public void markCancelFailed() {
        this.status = PaymentStatus.CANCEL_FAILED;
    }

    public boolean isPaid() {
        return status == PaymentStatus.PAID;
    }

    public boolean isCancelled() {
        return status == PaymentStatus.CANCELLED;
    }

    public boolean isInProgress() {
        return status == PaymentStatus.READY;
    }

    private void validateStatus(PaymentStatus status) {
        if (this.status != status) {
            throw new GeneralException(PaymentErrorCode.INVALID_STATUS_TRANSITION);
        }
    }
}
