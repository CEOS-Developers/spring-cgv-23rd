package com.ceos23.spring_cgv_23rd.Payment.Service.PaymentFacadeService;

import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class PaymentIdHandler {

    public String getPaymentId(String idHeader){
        return idHeader + "-" + UUID.randomUUID();
    }
}