package com.cgv.spring_boot.domain.payment.service;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ThreadLocalRandom;

@Component
public class PaymentIdGenerator {

    private static final DateTimeFormatter PAYMENT_ID_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmssSSS");

    public String generate() {
        int randomSuffix = ThreadLocalRandom.current().nextInt(1000, 10000);
        return PAYMENT_ID_FORMAT.format(LocalDateTime.now()) + "_" + randomSuffix;
    }
}
