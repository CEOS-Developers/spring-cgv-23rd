    package com.ceos23.spring_cgv_23rd.Payment.Service.PaymentFacadeService;

    import com.ceos23.spring_cgv_23rd.Payment.DTO.PaymentRequestDTO;
    import com.ceos23.spring_cgv_23rd.Payment.DTO.PaymentResponseDTO;
    import com.ceos23.spring_cgv_23rd.Payment.Domain.Payment;
    import com.ceos23.spring_cgv_23rd.Payment.Repository.PaymentRepository;
    import com.ceos23.spring_cgv_23rd.Payment.Service.ConcurrencyClient;
    import com.ceos23.spring_cgv_23rd.Payment.Service.PaymentDBService.MoviePaymentDBService;
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
    public class MoviePaymentFacadeService implements CancelablePaymentFacadeService {
        private final ConcurrencyClient concurrencyClient;
        private final PaymentRepository paymentRepository;
        private final MoviePaymentDBService moviePaymentDBService;
        private final PaymentService moviePaymentService;

        public Payment buy(PaymentRequestDTO req,
                           long targetId,
                           String userLoginId) {
            int retry = 0;
            Payment payment = moviePaymentDBService.setPayment(targetId, req, userLoginId);

            log.info("결제 시작: userId = {}, payType = {} paymentId = {}, userId = {}, targetId = {}, storeId = {}, orderName = {}, totalPayAmount = {}",
                    userLoginId, payment.getPayType(), payment.getPaymentId(), payment.getUserLoginId(), payment.getTargetId(), payment.getStoreId(), payment.getOrderName(), payment.getTotalPayAmount());


            while (retry < 5) {
                try {
                    retry++;
                    log.debug("결제 PG 요청 시작, paymentId = {}, retry >>> {}", payment.getPaymentId(), retry);

                    PaymentResponseDTO responseDTO = moviePaymentService.pay(payment, req);
                    return moviePaymentDBService.successPayment(payment.getId());

                } catch (CustomException ce) {
                    switch (ce.getCode()) {
                        case DUPLICATE_PAYMENT_ID:
                        case PAYMENT_FAILED_BY_OUTER_SERVER:
                            if (checkPaymentStatusPaid(payment.getPaymentId())) {
                                log.warn("외부연동에서 장애 발생으로 응답에 실패했으나 결제는 성공. paymentId: {}", payment.getPaymentId());
                                return moviePaymentDBService.successPayment(payment.getId());
                            } else {
                                //요청이 전달되지 않음
                                payment.paymentFail();
                                payment = moviePaymentDBService.setPayment(targetId, req, userLoginId);
                                continue;
                            }
                        default:
                            log.error("결제에 실패하였음. paymentId={}, errorMessage={}", payment.getPaymentId(), ce.getMessage());
                            moviePaymentDBService.failPayment(payment.getId());
                            throw ce;
                    }
                }
            }

            moviePaymentDBService.failPayment(payment.getId());
            log.error("결제에 실패하였음. paymentId={}", payment.getPaymentId());
            throw new CustomException(ErrorCode.PAYMENT_FAILED_BY_SERVER);
        }

        @Override
        public void cancel(long targetId){
            Payment payment = paymentRepository.findByTargetId(targetId).orElseThrow(
                    () -> new CustomException(ErrorCode.NOT_FOUND_PAYMENT)
            );

            moviePaymentDBService.cancelPayment(payment.getId());

            try {
                moviePaymentService.cancel(payment);
            } catch (CustomException ce) {
                if(checkPaymentStatusCancel(payment.getPaymentId())){
                    //요청이 제대로 전달되었음
                    //성공처리 필요
                } else {
                    //요청이 처리되지 않았음
                    throw ce;
                }
            }
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
