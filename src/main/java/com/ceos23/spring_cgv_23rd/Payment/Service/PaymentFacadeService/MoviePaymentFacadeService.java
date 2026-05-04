    package com.ceos23.spring_cgv_23rd.Payment.Service.PaymentFacadeService;

    import com.ceos23.spring_cgv_23rd.Payment.DTO.PaymentRequestDTO;
    import com.ceos23.spring_cgv_23rd.Payment.DTO.PaymentResponseDTO;
    import com.ceos23.spring_cgv_23rd.Payment.Domain.Payment;
    import com.ceos23.spring_cgv_23rd.Payment.Repository.PaymentRepository;
    import com.ceos23.spring_cgv_23rd.Payment.Service.ConcurrencyClient;
    import com.ceos23.spring_cgv_23rd.Payment.Service.PaymentDBService.MoviePaymentDBService;
    import com.ceos23.spring_cgv_23rd.Payment.Service.PaymentService.PaymentService;
    import com.ceos23.spring_cgv_23rd.Reservation.Repository.ReservationRepository;
    import com.ceos23.spring_cgv_23rd.global.Exception.CustomException;
    import com.ceos23.spring_cgv_23rd.global.Exception.ErrorCode;
    import feign.FeignException;
    import lombok.RequiredArgsConstructor;
    import org.springframework.stereotype.Service;

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

            while (retry < 5) {
                try {
                    retry++;
                    System.out.println("결제 시작!\n" + "retry >>> " + retry);

                    PaymentResponseDTO responseDTO = moviePaymentService.pay(payment, req);
                    return moviePaymentDBService.successPayment(payment.getId());

                } catch (CustomException ce) {
                    switch (ce.getCode()) {
                        case DUPLICATE_PAYMENT_ID:
                        case PAYMENT_FAILED_BY_OUTER_SERVER:
                            if (checkPaymentStatusPaid(payment.getPaymentId())) {
                                //요청이 제대로 전달되었음
                                //성공처리만 필요
                                return moviePaymentDBService.successPayment(payment.getId());
                            } else {
                                //요청이 전달되지 않음
                                continue;
                            }
                        default:
                            moviePaymentDBService.failPayment(payment.getId());
                            throw ce;
                    }
                }
            }

            moviePaymentDBService.failPayment(payment.getId());
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
