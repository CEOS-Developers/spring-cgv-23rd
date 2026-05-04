package com.ceos23.spring_cgv_23rd.Payment.Service.PaymentFacadeService;

import com.ceos23.spring_cgv_23rd.Payment.DTO.PaymentRequestDTO;
import com.ceos23.spring_cgv_23rd.Payment.Domain.Payment;
import feign.FeignException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

/**
 * TODO: 결제 완료 후 비즈니스 로직에서 오류 발생 시 롤백하기
 */
@Service
public interface PaymentFacadeService {
    Payment buy(PaymentRequestDTO req, long targetId, String userLoginId);
}


