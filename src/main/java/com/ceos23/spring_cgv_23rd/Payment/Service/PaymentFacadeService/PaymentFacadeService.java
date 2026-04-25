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
    Payment buy(PaymentRequestDTO req, long targetId);
    default boolean handleFeignException(FeignException fe){
        fe.printStackTrace();

        return switch (fe.status()) {
            case 403 ->
                    throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "결제 연동에 실패했습니다. (사유: 가맹점 storeId 불일치)");
            case 404 ->
                    throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "결제 연동에 실패했습니다. (사유: 존재하지 않는 가맹점)");
            case 409, 500 -> true;
            default -> throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, fe.getLocalizedMessage());
        };
    }
}


