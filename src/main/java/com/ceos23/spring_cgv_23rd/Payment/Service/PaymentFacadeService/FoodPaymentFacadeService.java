package com.ceos23.spring_cgv_23rd.Payment.Service.PaymentFacadeService;

import com.ceos23.spring_cgv_23rd.Payment.DTO.PaymentRequestDTO;
import com.ceos23.spring_cgv_23rd.Payment.DTO.PaymentResponseDTO;
import com.ceos23.spring_cgv_23rd.Payment.Domain.Payment;
import com.ceos23.spring_cgv_23rd.Payment.Service.ConcurrencyClient;
import com.ceos23.spring_cgv_23rd.Payment.Service.PaymentDBService.FoodPaymentDBService;
import com.ceos23.spring_cgv_23rd.Payment.Service.PaymentService.PaymentService;
import com.ceos23.spring_cgv_23rd.global.Exception.CustomException;
import com.ceos23.spring_cgv_23rd.global.Exception.ErrorCode;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class FoodPaymentFacadeService implements PaymentFacadeService {
    private final ConcurrencyClient concurrencyClient;
    private final FoodPaymentDBService foodPaymentDBService;
    private final PaymentService paymentService;

    @Override
    public Payment buy(PaymentRequestDTO req,
                       long targetId,
                       String userLoginId) {
        int retry = 0;

        Payment payment = foodPaymentDBService.setPayment(targetId, req, userLoginId);
        foodPaymentDBService.reflectBuying(payment.getPaymentId());

        log.info("결제 시작: userId = {}, payType = {} paymentId = {}, userId = {}, targetId = {}, storeId = {}, orderName = {}, totalPayAmount = {}",
                userLoginId, payment.getPayType(), payment.getPaymentId(), payment.getUserLoginId(), payment.getTargetId(), payment.getStoreId(), payment.getOrderName(), payment.getTotalPayAmount());

        while (retry < 5) {
            try {
                retry++;
                log.debug("결제 PG 요청 시작, paymentId = {}, retry >>> {}", payment.getPaymentId(), retry);

                paymentService.pay(payment, req);
                return payment;

            } catch (CustomException ce) {
                switch (ce.getCode()) {
                    case DUPLICATE_PAYMENT_ID:
                    case PAYMENT_FAILED_BY_OUTER_SERVER:
                        if (checkPaymentStatusPaid(payment.getPaymentId())) {
                            log.warn("외부연동에서 장애 발생으로 응답에 실패했으나 결제는 성공. paymentId: {}", payment.getPaymentId());
                            return payment;
                        } else {
                            log.warn("결제 실패. 재시도. errorMessage = {}", ce.getMessage());
                            foodPaymentDBService.changePaymentId(payment);
                            continue;
                        }
                    default:
                        log.error("결제에 실패하였음. paymentId={}, errorMessage={}", payment.getPaymentId(), ce.getMessage());
                        foodPaymentDBService.failPayment(payment.getPaymentId());
                        throw ce;
                }
            }
        }

        foodPaymentDBService.failPayment(payment.getPaymentId());
        log.error("결제에 실패하였음. paymentId={}", payment.getPaymentId());
        throw new CustomException(ErrorCode.PAYMENT_FAILED_BY_SERVER);
    }

    private boolean checkPaymentStatusPaid(String paymentId){
        try {
            PaymentResponseDTO paymentDTO = concurrencyClient.checkPayment(paymentId);
            return paymentDTO.findStatus("PAID");
        } catch (FeignException fe){
            return false;
        }
    }
}
