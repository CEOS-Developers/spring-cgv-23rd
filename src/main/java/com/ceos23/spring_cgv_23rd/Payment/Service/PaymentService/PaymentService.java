package com.ceos23.spring_cgv_23rd.Payment.Service.PaymentService;

import com.ceos23.spring_cgv_23rd.Payment.DTO.PaymentRequestDTO;
import com.ceos23.spring_cgv_23rd.Payment.DTO.PaymentResponseDTO;
import com.ceos23.spring_cgv_23rd.Payment.Domain.Payment;
import com.ceos23.spring_cgv_23rd.Payment.Service.ConcurrencyClient;
import com.ceos23.spring_cgv_23rd.global.Exception.CustomException;
import com.ceos23.spring_cgv_23rd.global.Exception.ErrorCode;
import feign.FeignException;
import org.springframework.stereotype.Service;

@Service
public class PaymentService {

    private final ConcurrencyClient concurrencyClient;

    public PaymentService(ConcurrencyClient concurrencyClient) {
        this.concurrencyClient = concurrencyClient;
    }

    public PaymentResponseDTO pay(Payment payment,
                                  PaymentRequestDTO req) {
        try {

            String paymentId = payment.getPaymentId();
            return concurrencyClient.pay(paymentId, req);

        } catch (FeignException fe) {
            switch (fe.status()) {
                case 403 -> throw new CustomException(ErrorCode.STORE_ID_MISMATCH);
                case 404 -> throw new CustomException(ErrorCode.STORE_NOT_FOUND);
                case 409 -> throw new CustomException(ErrorCode.DUPLICATE_PAYMENT_ID);
                case 500 -> throw new CustomException(ErrorCode.PAYMENT_FAILED_BY_OUTER_SERVER);
                default -> throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
            }
        }
    }

    public PaymentResponseDTO cancel(Payment payment){
        try {
            return concurrencyClient.cancel(payment.getPaymentId());
        } catch (FeignException fe) {
            switch (fe.status()) {
                case 404 -> throw new CustomException(ErrorCode.NOT_FOUND_PAYMENT);
                case 409 -> throw new CustomException(ErrorCode.PAYMENT_NOT_CANCELLABLE);
                default -> throw new CustomException(ErrorCode.CANCEL_FAILED_BY_OUTER_SERVER);
            }
        }
    }
}