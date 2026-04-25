package com.ceos23.spring_cgv_23rd.Payment.Service.PaymentFacadeService;

public interface CancelablePaymentFacadeService extends PaymentFacadeService {
    void cancel(long targetId);
}