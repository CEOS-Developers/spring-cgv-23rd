package com.ceos23.spring_boot.domain.store.service;

import com.ceos23.spring_boot.domain.store.dto.OrderCommand;
import com.ceos23.spring_boot.domain.store.dto.OrderInfo;
import com.ceos23.spring_boot.domain.store.dto.OrderItemCommand;
import com.ceos23.spring_boot.domain.store.entity.Inventory;
import com.ceos23.spring_boot.domain.store.entity.Menu;
import com.ceos23.spring_boot.domain.store.entity.Order;
import com.ceos23.spring_boot.domain.store.repository.InventoryRepository;
import com.ceos23.spring_boot.domain.store.repository.MenuRepository;
import com.ceos23.spring_boot.domain.store.repository.OrderItemRepository;
import com.ceos23.spring_boot.domain.store.repository.OrderRepository;
import com.ceos23.spring_boot.domain.theater.entity.Theater;
import com.ceos23.spring_boot.domain.theater.repository.TheaterRepository;
import com.ceos23.spring_boot.domain.user.entity.User;
import com.ceos23.spring_boot.domain.user.repository.UserRepository;
import com.ceos23.spring_boot.global.exception.BusinessException;
import com.ceos23.spring_boot.global.exception.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private TheaterRepository theaterRepository;

    @Mock
    private MenuRepository menuRepository;

    @Mock
    private InventoryRepository inventoryRepository;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderItemRepository orderItemRepository;

    @InjectMocks
    private OrderService orderService;

    @Test
    @DisplayName("매점 주문 성공: 주문이 성공하면 재고가 차감되고 총액이 계산된다.")
    void createOrder_Success() {
        // Given
        String userEmail = "user@naver.com";
        Long theaterId = 1L;
        Long popcornId = 1L;
        Long colaId = 2L;

        User user = User.builder().build();

        Theater theater = Theater.builder().name("CGV 강남").build();
        ReflectionTestUtils.setField(theater, "id", theaterId);

        Menu popcorn = Menu.builder()
                .name("팝콘")
                .price(5000)
                .build();
        ReflectionTestUtils.setField(popcorn, "id", popcornId);

        Menu cola = Menu.builder()
                .name("콜라")
                .price(2000)
                .build();
        ReflectionTestUtils.setField(cola, "id", colaId);

        Inventory popcornInventory = Inventory.builder()
                .stock(10)
                .build();

        Inventory colaInventory = Inventory.builder()
                .stock(10)
                .build();

        given(userRepository.findByEmail(userEmail)).willReturn(Optional.of(user));
        given(theaterRepository.findById(theaterId)).willReturn(Optional.of(theater));

        given(menuRepository.findById(popcornId)).willReturn(Optional.of(popcorn));
        given(menuRepository.findById(colaId)).willReturn(Optional.of(cola));

        given(inventoryRepository.findByTheaterIdAndMenuIdWithLock(theaterId, popcornId))
                .willReturn(Optional.of(popcornInventory));
        given(inventoryRepository.findByTheaterIdAndMenuIdWithLock(theaterId, colaId))
                .willReturn(Optional.of(colaInventory));

        List<OrderItemCommand> orderItems = List.of(
                new OrderItemCommand(popcornId, 1),
                new OrderItemCommand(colaId, 1)
        );
        OrderCommand command = new OrderCommand(userEmail, theaterId, orderItems);

        // When
        OrderInfo result = orderService.createOrder(command);

        // Then
        assertThat(result.totalPrice()).isEqualTo(7000);
        assertThat(result.orderItemInfos()).hasSize(2);

        assertThat(popcornInventory.getStock()).isEqualTo(9);
        assertThat(colaInventory.getStock()).isEqualTo(9);

        verify(orderRepository).save(any(Order.class));
        verify(orderItemRepository).saveAll(anyList());
    }

    @Test
    @DisplayName("매점 주문 실패: 재고가 부족할 경우 예외가 발생하고 주문은 저장되지 않는다.")
    void createOrder_Fail_OutOfStock() {
        // Given
        String userEmail = "user@naver.com";
        Long theaterId = 1L;
        Long popcornId = 1L;

        User user = User.builder().build();
        Theater theater = Theater.builder().build();
        ReflectionTestUtils.setField(theater, "id", theaterId);

        Menu popcorn = Menu.builder()
                .name("팝콘")
                .price(5000)
                .build();
        ReflectionTestUtils.setField(popcorn, "id", popcornId);

        Inventory popcornInventory = Inventory.builder()
                .stock(1)
                .build();

        given(userRepository.findByEmail(userEmail)).willReturn(Optional.of(user));
        given(theaterRepository.findById(theaterId)).willReturn(Optional.of(theater));
        given(menuRepository.findById(1L)).willReturn(Optional.of(popcorn));
        given(inventoryRepository.findByTheaterIdAndMenuIdWithLock(theaterId, popcornId))
                .willReturn(Optional.of(popcornInventory));

        List<OrderItemCommand> orderItems = List.of(new OrderItemCommand(popcornId, 2));
        OrderCommand command = new OrderCommand(userEmail, theaterId, orderItems);

        assertThatThrownBy(() -> orderService.createOrder(command))
                .isInstanceOf(BusinessException.class)
                .hasMessage(ErrorCode.OUT_OF_STOCK.getMessage());

        verify(orderRepository, never()).save(any());
        verify(orderItemRepository, never()).saveAll(anyList());
    }
}