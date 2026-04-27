package com.ceos.spring_cgv_23rd.domain.payment.application.port.in;

import com.ceos.spring_cgv_23rd.domain.payment.application.dto.command.PayCommand;
import com.ceos.spring_cgv_23rd.domain.payment.application.dto.result.PaymentResult;

public interface PaymentUseCase {

    PaymentResult pay(PayCommand command);
}
