package com.ceos23.spring_cgv_23rd.FoodOrder.Service;

import com.ceos23.spring_cgv_23rd.FoodOrder.DTO.CartResponseDTO;
import com.ceos23.spring_cgv_23rd.FoodOrder.DTO.FoodMenuAndQuantityDTO;
import com.ceos23.spring_cgv_23rd.FoodOrder.DTO.FoodOrderRequestDTO;
import com.ceos23.spring_cgv_23rd.FoodOrder.DTO.OrderResponseDTO;
import com.ceos23.spring_cgv_23rd.FoodOrder.Domain.Cart;
import com.ceos23.spring_cgv_23rd.FoodOrder.Repository.CartRepository;
import com.ceos23.spring_cgv_23rd.FoodOrder.Repository.FoodOrderRepository;
import com.ceos23.spring_cgv_23rd.Payment.DTO.PaymentRequestDTO;
import com.ceos23.spring_cgv_23rd.Payment.Service.PaymentFacadeService.PaymentFacadeService;
import com.ceos23.spring_cgv_23rd.Reservation.Domain.ReservationStatus;
import com.ceos23.spring_cgv_23rd.Theater.Domain.Theater;
import com.ceos23.spring_cgv_23rd.Theater.Domain.TheaterMenu;
import com.ceos23.spring_cgv_23rd.Theater.Repository.TheaterMenuRepository;
import com.ceos23.spring_cgv_23rd.Theater.Repository.TheaterRepository;
import com.ceos23.spring_cgv_23rd.User.Domain.User;
import com.ceos23.spring_cgv_23rd.User.Repository.UserRepository;
import com.ceos23.spring_cgv_23rd.global.Exception.CustomException;
import com.ceos23.spring_cgv_23rd.global.Exception.ErrorCode;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class FoodOrderService {

    private final TheaterRepository theaterRepository;
    private final CartRepository cartRepository;

    @Value("${concurrency.storeId}")
    private String storeId;

    /**
     * TODO: 같은 영화관 강제하기
     */
    private final TheaterMenuRepository theaterMenuRepository;
    private final UserRepository userRepository;
    private final FoodOrderRepository foodOrderRepository;
    private final PaymentFacadeService paymentService;

    public FoodOrderService(TheaterMenuRepository theaterMenuRepository,
                            UserRepository userRepository,
                            FoodOrderRepository foodOrderRepository,
                            @Qualifier("foodPaymentFacadeService") PaymentFacadeService paymentService,
                            TheaterRepository theaterRepository, CartRepository cartRepository) {
        this.theaterMenuRepository = theaterMenuRepository;
        this.userRepository = userRepository;
        this.foodOrderRepository = foodOrderRepository;
        this.paymentService = paymentService;
        this.theaterRepository = theaterRepository;
        this.cartRepository = cartRepository;
    }

    /**
     * 장바구니에 물건추가
     * @param loginId
     * @param foodOrderRequestDTO
     * @return
     */
    @Transactional
    public CartResponseDTO addItemToCart(String loginId, FoodOrderRequestDTO foodOrderRequestDTO){
        User user = userRepository.findByLoginId(loginId).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND_USER)
        );

        Theater theater = theaterRepository.findById(foodOrderRequestDTO.theaterId()).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND_THEATER)
        );

        Cart cart = cartRepository.findByUserAndStatus(user, ReservationStatus.RESERVED).orElse(
                Cart.create(user, theater)
        );

        for(FoodMenuAndQuantityDTO menuReqInfos : foodOrderRequestDTO.reqs()){
            TheaterMenu menu = theaterMenuRepository.findById(menuReqInfos.menuId()).orElseThrow(
                    () -> new CustomException(ErrorCode.NOT_FOUND_MENU)
            );

            cart.addItem(menu, menuReqInfos.quantity());
        }

        Cart savedCart = cartRepository.save(cart);
        return CartResponseDTO.create(savedCart);
    }

    @Transactional
    public CartResponseDTO buyCart(String loginId){
        User user = userRepository.findByLoginId(loginId).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND_USER)
        );

        Cart cart = cartRepository.findByUserAndStatus(user, ReservationStatus.RESERVED).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND_CART)
        );

        paymentService.buy(
                PaymentRequestDTO.create(cart, storeId, "음식주문"),
                cart.getId()
        );

        return CartResponseDTO.create(cartRepository.save(cart));
    }

    @Transactional
    public CartResponseDTO findCart(String loginId){
        User user = userRepository.findByLoginId(loginId).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND_USER)
        );

        Cart cart = cartRepository.findByUserAndStatus(user, ReservationStatus.RESERVED).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND_CART)
        );

        return CartResponseDTO.create(cart);
    }

    @Transactional
    public List<OrderResponseDTO> findPreviousOrder(String loginId){
        User user = userRepository.findByLoginId(loginId).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND_USER)
        );

        return OrderResponseDTO.create(foodOrderRepository.findAllByUser(user));
    }
}
