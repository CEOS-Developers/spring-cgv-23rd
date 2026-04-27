package com.ceos23.spring_cgv_23rd.Payment.Service.PaymentFacadeService;

import com.ceos23.spring_cgv_23rd.FoodOrder.Domain.Cart;
import com.ceos23.spring_cgv_23rd.FoodOrder.Domain.Order;
import com.ceos23.spring_cgv_23rd.FoodOrder.Repository.CartRepository;
import com.ceos23.spring_cgv_23rd.FoodOrder.Repository.FoodOrderRepository;
import com.ceos23.spring_cgv_23rd.Payment.DTO.PaymentRequestDTO;
import com.ceos23.spring_cgv_23rd.Payment.DTO.PaymentResponseDTO;
import com.ceos23.spring_cgv_23rd.Payment.Domain.PayType;
import com.ceos23.spring_cgv_23rd.Payment.Domain.Payment;
import com.ceos23.spring_cgv_23rd.Payment.Repository.PaymentRepository;
import com.ceos23.spring_cgv_23rd.Payment.Service.ConcurrencyClient;
import com.ceos23.spring_cgv_23rd.Payment.Service.PaymentDBService.FoodPaymentDBService;
import com.ceos23.spring_cgv_23rd.Payment.Service.PaymentService.PaymentService;
import com.ceos23.spring_cgv_23rd.global.Exception.CustomException;
import com.ceos23.spring_cgv_23rd.global.Exception.ErrorCode;
import feign.FeignException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class FoodPaymentFacadeService implements PaymentFacadeService {
    private final PaymentRepository paymentRepository;
    private final CartRepository cartRepository;
    private final PaymentIdHandler paymentIdHandler;
    private final ConcurrencyClient concurrencyClient;
    private final FoodOrderRepository foodOrderRepository;
    private final FoodPaymentDBService foodPaymentDBService;
    private final PaymentService paymentService;

    public FoodPaymentFacadeService(PaymentRepository paymentRepository,
                                    CartRepository cartRepository,
                                    PaymentIdHandler paymentIdHandler,
                                    ConcurrencyClient concurrencyClient, FoodOrderRepository foodOrderRepository, FoodPaymentDBService foodPaymentDBService, PaymentService paymentService) {
        this.paymentRepository = paymentRepository;
        this.cartRepository = cartRepository;
        this.paymentIdHandler = paymentIdHandler;
        this.concurrencyClient = concurrencyClient;
        this.foodOrderRepository = foodOrderRepository;
        this.foodPaymentDBService = foodPaymentDBService;
        this.paymentService = paymentService;
    }

    @Override
    @Transactional
    public Payment buy(PaymentRequestDTO req,
                       long targetId) {
        int retry = 0;

        Payment payment = foodPaymentDBService.setPayment(targetId, req);

        while (retry < 5) {
            try {
                retry++;
                System.out.println("결제 시작!\n" + "retry >>> " + retry);

                paymentService.pay(payment, req);
                return foodPaymentDBService.successPayment(payment.getId());

            } catch (CustomException ce) {
                switch (ce.getCode()) {
                    case DUPLICATE_PAYMENT_ID:
                    case PAYMENT_FAILED_BY_OUTER_SERVER:
                        if (checkPaymentStatusPaid(payment.getPaymentId())) {
                            //요청이 제대로 전달되었음
                            //성공처리만 필요
                            return foodPaymentDBService.successPayment(payment.getTargetId());
                        } else {
                            //요청이 전달되지 않음
                            continue;
                        }
                    default:
                        foodPaymentDBService.failPayment(payment.getId());
                        throw ce;
                }
            }
        }

        foodPaymentDBService.failPayment(payment.getId());
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

    private boolean checkPaymentStatusCancel(String paymentId){
        try {
            PaymentResponseDTO paymentDTO = concurrencyClient.checkPayment(paymentId);
            return paymentDTO.findStatus("CANCELLED");
        } catch (FeignException fe){
            return false;
        }
    }
}
