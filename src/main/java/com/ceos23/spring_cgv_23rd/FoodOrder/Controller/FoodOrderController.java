package com.ceos23.spring_cgv_23rd.FoodOrder.Controller;

import com.ceos23.spring_cgv_23rd.FoodOrder.DTO.CartResponseDTO;
import com.ceos23.spring_cgv_23rd.FoodOrder.DTO.FoodOrderRequestDTO;
import com.ceos23.spring_cgv_23rd.FoodOrder.DTO.OrderResponseDTO;
import com.ceos23.spring_cgv_23rd.FoodOrder.Service.FoodOrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/foods")
public class FoodOrderController {

    private final FoodOrderService foodOrderService;

    public FoodOrderController(FoodOrderService foodOrderService) {
        this.foodOrderService = foodOrderService;
    }

    /**
     * 음식 추가하기
     * (장바구니 담기)
     */
    @PostMapping("/items")
    public ResponseEntity<CartResponseDTO> addItemToOrder(
            @AuthenticationPrincipal User user,
            @RequestBody FoodOrderRequestDTO forDTO
    ){
        return ResponseEntity.ok(foodOrderService.addItemToCart(user.getUsername(), forDTO));
    }

    /**
     * 예약하기
     *
     * @return 성공적인 예약을 축하하는 메시지..
     */
    @PostMapping("/pay")
    public ResponseEntity<CartResponseDTO> buyOrder(
            @AuthenticationPrincipal User user
            ){
        return ResponseEntity.ok(foodOrderService.buyCart(user.getUsername()));
    }

    /**
     * 예약조회하기
     */
    @GetMapping
    public ResponseEntity<CartResponseDTO> findOrder(
            @AuthenticationPrincipal User user
    ){
        return ResponseEntity.ok(foodOrderService.findCart(user.getUsername()));
    }

    @GetMapping("/history")
    public ResponseEntity<List<OrderResponseDTO>> findPreviousOrder(
            @AuthenticationPrincipal User user
    ){
        return ResponseEntity.ok(foodOrderService.findPreviousOrder(user.getUsername()));
    }
}