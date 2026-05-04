package com.ceos23.spring_cgv_23rd.FoodOrder.Service;

import com.ceos23.spring_cgv_23rd.FoodOrder.DTO.CartResponseDTO;
import com.ceos23.spring_cgv_23rd.FoodOrder.DTO.FoodMenuAndQuantityDTO;
import com.ceos23.spring_cgv_23rd.FoodOrder.DTO.FoodOrderRequestDTO;
import com.ceos23.spring_cgv_23rd.FoodOrder.DTO.OrderResponseDTO;
import com.ceos23.spring_cgv_23rd.FoodOrder.Domain.Cart;
import com.ceos23.spring_cgv_23rd.FoodOrder.Repository.CartRepository;
import com.ceos23.spring_cgv_23rd.FoodOrder.Repository.FoodOrderRepository;
import com.ceos23.spring_cgv_23rd.Payment.DTO.PaymentRequestDTO;
import com.ceos23.spring_cgv_23rd.Payment.Service.PaymentDBService.FoodPaymentDBService;
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
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FoodOrderService{

    private final TheaterRepository theaterRepository;
    private final CartRepository cartRepository;
    private final TheaterMenuRepository theaterMenuRepository;
    private final UserRepository userRepository;
    private final FoodOrderRepository foodOrderRepository;
    private final PaymentFacadeService paymentFacadeService;
    private final FoodPaymentDBService foodPaymentDBService;

    @Value("${concurrency.storeId}")
    private String storeId;

    public FoodOrderService(TheaterMenuRepository theaterMenuRepository,
                            UserRepository userRepository,
                            FoodOrderRepository foodOrderRepository,
                            @Qualifier("foodPaymentFacadeService") PaymentFacadeService paymentService,
                            TheaterRepository theaterRepository,
                            CartRepository cartRepository,
                            FoodPaymentDBService foodPaymentDBService) {
        this.theaterMenuRepository = theaterMenuRepository;
        this.userRepository = userRepository;
        this.foodOrderRepository = foodOrderRepository;
        this.paymentFacadeService = paymentService;
        this.theaterRepository = theaterRepository;
        this.cartRepository = cartRepository;
        this.foodPaymentDBService = foodPaymentDBService;
    }

    /**
     * 장바구니에 물건 추가하기
     *
     * @param loginId 로그인 시 사용되는 유저ID
     * @param foodOrderRequestDTO 사용자 요청정보. 영화관ID 및 음식의 ID와 수량에 대한 정보를 List로 전달합니다.
     * @return 현재 장바구니 정보를 반환합니다.
     */
    @Transactional
    public CartResponseDTO addItemToCart(String loginId, FoodOrderRequestDTO foodOrderRequestDTO){

        User user = userRepository.findByLoginId(loginId).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND_USER)
        );

        Theater theater = theaterRepository.findById(foodOrderRequestDTO.theaterId()).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND_THEATER)
        );

        Cart cart = cartRepository
                .findByUserAndTheaterAndStatus(user, theater, ReservationStatus.RESERVED)
                .orElseGet(() -> Cart.create(user, theater));
        cartRepository.save(cart);

        for(FoodMenuAndQuantityDTO menuReqInfos : foodOrderRequestDTO.reqs()){
            TheaterMenu menu = theaterMenuRepository.findById(menuReqInfos.menuId()).orElseThrow(
                    () -> new CustomException(ErrorCode.NOT_FOUND_MENU)
            );

            try {
                cart.addItem(menu, menuReqInfos.quantity());
            } catch (CustomException ce){
                log.warn("재고 부족: menuId={}, 사용자 주문 수량={}", menuReqInfos.menuId(), menuReqInfos.quantity());
                throw ce;
            }

            log.debug("장바구니 물건 추가: 사용자={}, 물건ID={}, 수량={}", user.getLoginId(), menuReqInfos.menuId(), menuReqInfos.quantity());
        }

        String items = foodOrderRequestDTO.reqs().stream()
                .map(r -> r.menuId() + ":" + r.quantity())
                .collect(Collectors.joining(", "));

        log.info("장바구니 물건 추가 완료: userId= {}, theater= {}, items=[{}]", user.getLoginId(), theater.getId(), items);
        return CartResponseDTO.create(cart);
    }

    /**
     * 사용자의 장바구니 결제하기
     *
     * @param loginId 로그인 시 사용되는 유저ID
     * @return 결제된 장바구니의 정보를 반환합니다.
     */
    public CartResponseDTO buyCart(String loginId){
        log.info("장바구니 결제 요청 시작: userId={}", loginId);

        User user = foodPaymentDBService.findUser(loginId);
        Cart cart = foodPaymentDBService.findActivatedCartByUser(user);

        paymentFacadeService.buy(
                PaymentRequestDTO.create(cart, storeId, "음식주문"),
                cart.getId(),
                user.getLoginId()
        );

        log.info("결제완료: userId={}, cartId={}", loginId, cart.getId());
        return CartResponseDTO.create(cart);
    }

    /**
     * 사용자의 현재 장바구니를 조회합니다.
     *
     * @param loginId 로그인 시 사용되는 유저ID
     * @return 현재 활성화된 장바구니의 정보를 반환합니다.
     */
    @Transactional(readOnly = true)
    public CartResponseDTO findCart(String loginId){
        User user = userRepository.findByLoginId(loginId).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND_USER)
        );

        Cart cart = cartRepository.findByUserAndStatus(user, ReservationStatus.RESERVED).orElse(
                null
        );

        if (cart == null){
            log.debug("빈 장바구니 확인됨: userId={}", user.getLoginId());
            return CartResponseDTO.empty();
        }

        log.debug("장바구니 확인됨: userId={}", user.getLoginId());
        return CartResponseDTO.create(cart);
    }

    /**
     * 사용자의 이전 구매기록을 조회합니다.
     *
     * @param loginId 로그인 시 사용되는 유저ID
     * @return 사용자의 과거 거래기록을 반환합니다.
     */
    @Transactional(readOnly = true)
    public List<OrderResponseDTO> findPreviousOrder(String loginId){
        User user = userRepository.findByLoginId(loginId).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND_USER)
        );

        log.debug("이전에 결제한 장바구니 확인됨: userId={}", user.getLoginId());
        return OrderResponseDTO.create(foodOrderRepository.findAllByUser(user));
    }
}
