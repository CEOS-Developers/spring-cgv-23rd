package com.ceos23.spring_cgv_23rd.Payment.Service.PaymentDBService;

import com.ceos23.spring_cgv_23rd.FoodOrder.Domain.Cart;
import com.ceos23.spring_cgv_23rd.FoodOrder.Domain.CartItem;
import com.ceos23.spring_cgv_23rd.FoodOrder.Domain.Order;
import com.ceos23.spring_cgv_23rd.FoodOrder.Domain.OrderItem;
import com.ceos23.spring_cgv_23rd.FoodOrder.Repository.CartRepository;
import com.ceos23.spring_cgv_23rd.FoodOrder.Repository.FoodOrderRepository;
import com.ceos23.spring_cgv_23rd.Payment.DTO.PaymentRequestDTO;
import com.ceos23.spring_cgv_23rd.Payment.Domain.PayType;
import com.ceos23.spring_cgv_23rd.Payment.Domain.Payment;
import com.ceos23.spring_cgv_23rd.Payment.Repository.PaymentRepository;
import com.ceos23.spring_cgv_23rd.Payment.Service.PaymentFacadeService.PaymentIdHandler;
import com.ceos23.spring_cgv_23rd.Reservation.Domain.ReservationStatus;
import com.ceos23.spring_cgv_23rd.Theater.Repository.TheaterMenuRepository;
import com.ceos23.spring_cgv_23rd.User.Repository.UserRepository;
import com.ceos23.spring_cgv_23rd.global.Exception.CustomException;
import com.ceos23.spring_cgv_23rd.global.Exception.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.PessimisticLockException;
import org.springframework.dao.CannotAcquireLockException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Service
public class FoodPaymentDBService extends DefaultPaymentDBService{
    private final PaymentIdHandler paymentIdHandler;
    private final CartRepository cartRepository;
    private final PaymentRepository paymentRepository;
    private final FoodOrderRepository foodOrderRepository;
    private final TheaterMenuRepository theaterMenuRepository;

    public FoodPaymentDBService(UserRepository userRepository, CartRepository cartRepository, PaymentIdHandler paymentIdHandler, CartRepository cartRepository1, PaymentRepository paymentRepository, FoodOrderRepository foodOrderRepository, TheaterMenuRepository theaterMenuRepository) {
        super(userRepository, cartRepository);
        this.paymentIdHandler = paymentIdHandler;
        this.cartRepository = cartRepository1;
        this.paymentRepository = paymentRepository;
        this.foodOrderRepository = foodOrderRepository;
        this.theaterMenuRepository = theaterMenuRepository;
    }

    @Transactional(readOnly = true)
    public void payAvailable(long cartId){
        Cart cart = cartRepository.findById(cartId).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND_CART)
        );
    }

    @Transactional
    public Payment setPayment(long targetId,
                              PaymentRequestDTO req,
                              String userLoginId){
        String paymentId = paymentIdHandler.getPaymentId("FoodOrder");

        Cart cart = cartRepository.findByIdAndStatus(targetId, ReservationStatus.RESERVED).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND_CART)
        );

        Optional<CartItem> cartItemOptional = cart.checkingUnavailableCartItem();
        if(cartItemOptional.isPresent()){
            CartItem cartItem = cartItemOptional.get();
            log.warn("재고가 부족합니다. 메뉴이름: {}, 재고: {}, 사용자주문양: {}", cartItem.getMenu().getId(), cartItem.getMenu().getSold(), cartItem.getQuantity());
            throw new CustomException(ErrorCode.INVENTORY_SHORTAGE);
        }

        Payment payment = Payment.create(userLoginId, paymentId, req.storeId(), req.orderName(), req.totalPayAmount(), req.currency(), PayType.ORDER, cart.getId());
        return paymentRepository.save(payment);
    }

    @Transactional
    public void failPayment(String paymentId){
        Payment payment = paymentRepository.findByPaymentId(paymentId).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND_PAYMENT)
        );

        if (!payment.isFailed()){
            return;
        }

        Cart cart = cartRepository.findByIdAndStatus(payment.getTargetId(), ReservationStatus.RESERVED).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND_CART)
        );

        try {
            for (CartItem item : cart.getCartItems()) {
                theaterMenuRepository.increaseStock(item.getMenu().getId(), item.getQuantity());
            }

            cart.cancel();
            payment.paymentFail();
            log.info("결제취소됨. paymentId: {}", payment.getPaymentId());

        } catch (CannotAcquireLockException e) {
            log.error("Deadlock 발생 - paymentId={}, userId={}",
                    payment.getPaymentId(), payment.getUserLoginId(), e);
            throw e;
        }
    }

    @Transactional
    public void changePaymentId(Payment payment){
        payment.updatePaymentId(paymentIdHandler.getPaymentId("FoodOrder"));
    }

    @Transactional
    public void reflectBuying(String paymentId){
        Payment payment = paymentRepository.findByPaymentId(paymentId).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND_PAYMENT)
        );

        if (foodOrderRepository.existsByPaymentPaymentId(paymentId)) {
            log.info("이미 결제가 완료된 paymentId입니다. paymentId: {}", paymentId);
            throw new CustomException(ErrorCode.PAYMENT_ALREADY_PAID);
        }

        Cart cart = cartRepository.findByIdAndStatus(payment.getTargetId(), ReservationStatus.RESERVED).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND_CART)
        );

        try {
            Order order = Order.create(payment, cart);

            for (CartItem item : cart.getCartItems()) {
                int status = theaterMenuRepository.decreaseStock(item.getMenu().getId(), item.getQuantity());

                if (status == 0) {
                    log.info("재고부족: menuId={}, sold={}, userReqSold={}", item.getMenu().getId(), item.getMenu().getSold(), item.getMenu());
                    throw new CustomException(ErrorCode.INVENTORY_SHORTAGE);
                }

                order.addOrderItem(new OrderItem(item));
            }

            payment.paymentSuccess();
            cart.endPaying();

            log.info("결제성공. paymentId: {}, orderId: {}", payment.getPaymentId(), order.getId());

            foodOrderRepository.save(order);
            cartRepository.save(cart);
            paymentRepository.save(payment);
        } catch (CannotAcquireLockException |
                 PessimisticLockException e) {
            log.error("Deadlock 발생: paymentId={}, userId={}", payment.getPaymentId(), payment.getUserLoginId());
            throw e;
        }
    }
}
