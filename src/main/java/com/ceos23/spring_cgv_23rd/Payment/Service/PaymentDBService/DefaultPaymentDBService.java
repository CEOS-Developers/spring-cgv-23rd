package com.ceos23.spring_cgv_23rd.Payment.Service.PaymentDBService;

import com.ceos23.spring_cgv_23rd.FoodOrder.Domain.Cart;
import com.ceos23.spring_cgv_23rd.FoodOrder.Repository.CartRepository;
import com.ceos23.spring_cgv_23rd.Reservation.Domain.ReservationStatus;
import com.ceos23.spring_cgv_23rd.User.Domain.User;
import com.ceos23.spring_cgv_23rd.User.Repository.UserRepository;
import com.ceos23.spring_cgv_23rd.global.Exception.CustomException;
import com.ceos23.spring_cgv_23rd.global.Exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DefaultPaymentDBService {

    private final UserRepository userRepository;
    private final CartRepository cartRepository;

    @Transactional(readOnly = true)
    public User findUser(String userLoginId){
        return userRepository.findByLoginId(userLoginId).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND_USER)
        );
    }

    @Transactional(readOnly = true)
    public Cart findActivatedCartByUser(User user){
        return cartRepository.findByUserAndStatus(user, ReservationStatus.RESERVED).orElseThrow(
                () -> new CustomException(ErrorCode.EMPTY_CART)
        );
    }
}
