package com.ceos23.spring_cgv_23rd.Payment.Service.PaymentDBService;

import com.ceos23.spring_cgv_23rd.FoodOrder.Domain.Cart;
import com.ceos23.spring_cgv_23rd.FoodOrder.Domain.Order;
import com.ceos23.spring_cgv_23rd.FoodOrder.Repository.CartRepository;
import com.ceos23.spring_cgv_23rd.FoodOrder.Repository.FoodOrderRepository;
import com.ceos23.spring_cgv_23rd.Payment.DTO.PaymentRequestDTO;
import com.ceos23.spring_cgv_23rd.Payment.Domain.PayType;
import com.ceos23.spring_cgv_23rd.Payment.Domain.Payment;
import com.ceos23.spring_cgv_23rd.Payment.Repository.PaymentRepository;
import com.ceos23.spring_cgv_23rd.Payment.Service.PaymentFacadeService.PaymentIdHandler;
import com.ceos23.spring_cgv_23rd.global.Exception.CustomException;
import com.ceos23.spring_cgv_23rd.global.Exception.ErrorCode;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class FoodPaymentDBService {

    private final PaymentIdHandler paymentIdHandler;
    private final CartRepository cartRepository;
    private final PaymentRepository paymentRepository;
    private final FoodOrderRepository foodOrderRepository;

    public FoodPaymentDBService(PaymentIdHandler paymentIdHandler, CartRepository cartRepository, PaymentRepository paymentRepository, FoodOrderRepository foodOrderRepository) {
        this.paymentIdHandler = paymentIdHandler;
        this.cartRepository = cartRepository;
        this.paymentRepository = paymentRepository;
        this.foodOrderRepository = foodOrderRepository;
    }

    @Transactional
    public Payment setPayment(long targetId,
                           PaymentRequestDTO req){
        String paymentId = paymentIdHandler.getPaymentId();
        Cart cart = cartRepository.findById(targetId).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND_CART)
        );

        if (!cart.isAvailable()){
            throw new CustomException(ErrorCode.CART_IS_UNAVAILABLE);
        }

        cart.startBuying();
        Payment payment = Payment.create(paymentId, req.storeId(), req.orderName(), req.totalPayAmount(), req.currency(), PayType.ORDER, cart.getId());
        return paymentRepository.save(payment);
    }

    @Transactional
    public void failPayment(long id){
        Payment payment = paymentRepository.findById(id).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND_PAYMENT)
        );

        payment.paymentFail();
    }

    @Transactional
    public Payment successPayment(long id){
        Payment payment = paymentRepository.findById(id).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND_PAYMENT)
        );

        Cart cart = cartRepository.findById(payment.getTargetId()).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND_CART)
        );

        payment.paymentSuccess();
        Order order = cart.buyCart();
        foodOrderRepository.save(order);

        return payment;
    }

    /*
    @Transactional
    public Payment successPayment(long id){
        Payment payment = paymentRepository.findById(id).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND_PAYMENT)
        );
        Reservation reservation = reservationRepository.findById(payment.getTargetId()).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND_RESERVATION)
        );

        payment.paymentSuccess();
        reservation.buyReservation();

        return payment;
    }
     */

        /*
        @Transactional
    public void cancelPayment(long id){
        Payment payment = paymentRepository.findById(id).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND_PAYMENT)
        );
        Reservation reservation = reservationRepository.findById(payment.getTargetId()).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND_RESERVATION)
        );

        payment.cancel();
        reservation.cancel();
    }
         */
}
