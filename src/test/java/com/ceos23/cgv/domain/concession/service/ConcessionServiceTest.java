package com.ceos23.cgv.domain.concession.service;

import com.ceos23.cgv.domain.cinema.entity.Cinema;
import com.ceos23.cgv.domain.cinema.repository.CinemaRepository;
import com.ceos23.cgv.domain.concession.dto.FoodOrderRequest;
import com.ceos23.cgv.domain.concession.entity.FoodOrder;
import com.ceos23.cgv.domain.concession.entity.OrderItem;
import com.ceos23.cgv.domain.concession.entity.Product;
import com.ceos23.cgv.domain.concession.repository.FoodOrderRepository;
import com.ceos23.cgv.domain.concession.repository.OrderItemRepository;
import com.ceos23.cgv.domain.concession.repository.ProductRepository;
import com.ceos23.cgv.domain.user.entity.User;
import com.ceos23.cgv.domain.user.repository.UserRepository;
import com.ceos23.cgv.global.exception.CustomException;
import com.ceos23.cgv.global.exception.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ConcessionServiceTest {

    @Mock
    private FoodOrderRepository foodOrderRepository;
    @Mock
    private OrderItemRepository orderItemRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private CinemaRepository cinemaRepository;
    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ConcessionService concessionService;

    @Test
    @DisplayName("매점 주문 시 팝콘 2개와 콜라 1개의 총액이 정상적으로 계산되어 저장된다")
    void createOrder_Success_TotalPriceCalculated() {
        // Given (준비)
        User user = User.builder().id(1L).nickname("우혁").build();
        Cinema cinema = Cinema.builder().id(1L).name("CGV 신촌").build();

        // 팝콘(5,000원)과 콜라(3,000원) 엔티티 모킹
        Product popcorn = Product.builder().id(1L).name("달콤 팝콘").price(5000).build();
        Product cola = Product.builder().id(2L).name("콜라").price(3000).build();

        // 팝콘 2개, 콜라 1개 주문 요청 (기대 총액 = 5000*2 + 3000*1 = 13000원)
        FoodOrderRequest request = new FoodOrderRequest(
                1L, 1L,
                List.of(
                        new FoodOrderRequest.OrderItemRequest(1L, 2), // 팝콘 2개
                        new FoodOrderRequest.OrderItemRequest(2L, 1)  // 콜라 1개
                )
        );

        given(userRepository.findById(1L)).willReturn(Optional.of(user));
        given(cinemaRepository.findById(1L)).willReturn(Optional.of(cinema));
        given(productRepository.findById(1L)).willReturn(Optional.of(popcorn));
        given(productRepository.findById(2L)).willReturn(Optional.of(cola));

        // save 메서드 호출 시 인자로 넘어온 엔티티를 그대로 반환하도록 처리
        given(foodOrderRepository.save(any(FoodOrder.class))).willAnswer(invocation -> invocation.getArgument(0));

        // When (실행)
        FoodOrder savedOrder = concessionService.createOrder(request);

        // Then (검증)
        assertThat(savedOrder.getTotalPrice()).isEqualTo(13000);
        assertThat(savedOrder.getUser().getNickname()).isEqualTo("우혁");

        // 주문(FoodOrder) 저장은 로직 특성상 2번(초기화 시 1번, 총액 업데이트 후 1번) 발생함
        verify(foodOrderRepository, times(2)).save(any(FoodOrder.class));
        // 상품 종류가 2가지(팝콘, 콜라)이므로 OrderItem은 2번 저장되어야 함
        verify(orderItemRepository, times(2)).save(any(OrderItem.class));
    }

    @Test
    @DisplayName("존재하지 않는 매점 상품을 주문하려고 하면 PRODUCT_NOT_FOUND 예외가 발생한다")
    void createOrder_Fail_ProductNotFound() {
        // Given (준비)
        User user = User.builder().id(1L).nickname("우혁").build();
        Cinema cinema = Cinema.builder().id(1L).name("CGV 신촌").build();

        // 없는 상품(999번) 주문 요청
        FoodOrderRequest request = new FoodOrderRequest(
                1L, 1L,
                List.of(new FoodOrderRequest.OrderItemRequest(999L, 1))
        );

        given(userRepository.findById(1L)).willReturn(Optional.of(user));
        given(cinemaRepository.findById(1L)).willReturn(Optional.of(cinema));

        // 상품 조회 시 Optional.empty() 반환
        given(productRepository.findById(999L)).willReturn(Optional.empty());

        // When (실행) & Then (검증)
        CustomException exception = assertThrows(CustomException.class, () -> {
            concessionService.createOrder(request);
        });

        // 예외가 의도한 에러 코드(PRODUCT_NOT_FOUND)인지 확인
        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.PRODUCT_NOT_FOUND);
    }
}