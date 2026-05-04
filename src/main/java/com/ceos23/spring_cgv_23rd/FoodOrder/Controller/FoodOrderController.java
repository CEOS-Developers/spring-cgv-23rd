package com.ceos23.spring_cgv_23rd.FoodOrder.Controller;

import com.ceos23.spring_cgv_23rd.FoodOrder.DTO.CartResponseDTO;
import com.ceos23.spring_cgv_23rd.FoodOrder.DTO.FoodOrderRequestDTO;
import com.ceos23.spring_cgv_23rd.FoodOrder.DTO.OrderResponseDTO;
import com.ceos23.spring_cgv_23rd.FoodOrder.Service.FoodOrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/foods")
@RequiredArgsConstructor
public class FoodOrderController {
    private final FoodOrderService foodOrderService;

    /**
     * 장바구니에 음식을 추가합니다.
     *
     * @param user 사용자 정보. 로그인 되어있다면 직접 쿠키에서 가져옵니다.
     * @param forDTO 사용자 요청정보. 영화관ID 및 음식의 ID와 수량에 대한 정보를 List로 전달합니다.
     * @return 현재 장바구니 정보를 반환합니다.
     */
    @PostMapping("/items")
    public ResponseEntity<CartResponseDTO> addItemToOrder(
            @AuthenticationPrincipal User user,
            @RequestBody FoodOrderRequestDTO forDTO
    ){
        return ResponseEntity.ok(foodOrderService.addItemToCart(user.getUsername(), forDTO));
    }

    /**
     * 사용자의 장바구니를 결제합니다.
     *
     * @param user 사용자 정보. 로그인이 되어있다면 직접 쿠키에서 가져옵니다.
     * @return 결제된 장바구니의 정보를 반환합니다.
     */
    @PostMapping("/pay")
    public ResponseEntity<CartResponseDTO> buyOrder(
            @AuthenticationPrincipal User user
            ){
        return ResponseEntity.ok(foodOrderService.buyCart(user.getUsername()));
    }

    /**
     * 사용자 별 장바구니를 조회합니다.
     *
     * @param user 사용자 정보. 로그인이 되어있다면 직접 쿠키에서 가져옵니다.
     * @return 사용자의 현재 활성화된 장바구니의 정보를 반환합니다.
     */
    @GetMapping
    public ResponseEntity<CartResponseDTO> findCart(
            @AuthenticationPrincipal User user
    ){
        return ResponseEntity.ok(foodOrderService.findCart(user.getUsername()));
    }

    /**
     * 사용자의 이전 주문기록을 조회합니다.
     *
     * @param user 용자 정보. 로그인이 되어있다면 직접 쿠키에서 가져옵니다.
     * @return 사용자의 현재 이전 주문기록의 정보를 반환합니다.
     */
    @GetMapping("/history")
    public ResponseEntity<List<OrderResponseDTO>> findPreviousOrder(
            @AuthenticationPrincipal User user
    ){
        return ResponseEntity.ok(foodOrderService.findPreviousOrder(user.getUsername()));
    }
}