package com.ceos23.spring_cgv_23rd.Payment.Service.PaymentFacadeService;

import com.ceos23.spring_cgv_23rd.Payment.Domain.PaymentId;
import com.ceos23.spring_cgv_23rd.Payment.Repository.PaymentIdRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Component
public class PaymentIdHandler {
    private final PaymentIdRepository paymentIdRepository;

    public PaymentIdHandler(PaymentIdRepository paymentIdRepository) {
        this.paymentIdRepository = paymentIdRepository;
    }

    @Transactional
    public String getPaymentId(){
        PaymentId paymentId = paymentIdRepository.findForUpdate(LocalDate.now()).orElseGet(
                () -> {
                    try {
                        return paymentIdRepository.save(new PaymentId(LocalDate.now(), 1));
                    } catch (Exception e){
                        return paymentIdRepository.findForUpdate(LocalDate.now()).get();
                    }
                }
        );

        paymentId.setNextSeq();
        return paymentId.getCompletePaymentId();
    }
}