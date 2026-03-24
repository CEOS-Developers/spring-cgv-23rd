package com.ceos23.cgv.domain.concession.service;

import com.ceos23.cgv.domain.cinema.entity.Cinema;
import com.ceos23.cgv.domain.cinema.repository.CinemaRepository;
import com.ceos23.cgv.domain.concession.dto.InventoryUpdateRequest;
import com.ceos23.cgv.domain.concession.entity.Inventory;
import com.ceos23.cgv.domain.concession.entity.Product;
import com.ceos23.cgv.domain.concession.repository.InventoryRepository;
import com.ceos23.cgv.domain.concession.repository.ProductRepository;
import com.ceos23.cgv.global.exception.CustomException;
import com.ceos23.cgv.global.exception.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class InventoryServiceTest {

    @Mock
    private InventoryRepository inventoryRepository;
    @Mock
    private CinemaRepository cinemaRepository;
    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private InventoryService inventoryService;

    @Test
    @DisplayName("기존 재고가 있을 때 수량이 정상적으로 업데이트(차감) 된다")
    void updateInventory_Success() {
        // Given (준비)
        InventoryUpdateRequest request = new InventoryUpdateRequest(1L, 1L, -5); // 5개 차감 요청

        Cinema cinema = Cinema.builder().id(1L).name("CGV 강남").build();
        Product product = Product.builder().id(1L).name("고소팝콘").build();

        // 기존에 재고가 10개 있다고 가정
        Inventory existingInventory = Inventory.builder()
                .id(1L).cinema(cinema).product(product).stockQuantity(10).build();

        given(cinemaRepository.findById(1L)).willReturn(Optional.of(cinema));
        given(productRepository.findById(1L)).willReturn(Optional.of(product));
        given(inventoryRepository.findByCinemaIdAndProductId(1L, 1L)).willReturn(Optional.of(existingInventory));

        // save 될 때 들어온 엔티티를 그대로 반환하도록 설정
        given(inventoryRepository.save(any(Inventory.class))).willAnswer(invocation -> invocation.getArgument(0));

        // When (실행)
        Inventory updatedInventory = inventoryService.updateInventory(request);

        // Then (검증)
        // 10개에서 5개를 뺐으니 남은 재고는 5개여야 함!
        assertThat(updatedInventory.getStockQuantity()).isEqualTo(5);
        verify(inventoryRepository).save(any(Inventory.class));
    }

    @Test
    @DisplayName("재고 차감 시 결과가 1 미만으로 떨어지면 INVENTORY_SHORTAGE 예외가 발생한다")
    void updateInventory_Fail_Shortage() {
        // Given (준비)
        InventoryUpdateRequest request = new InventoryUpdateRequest(1L, 1L, -10); // 10개 차감 요청 (재고 부족 상황 유도)

        Cinema cinema = Cinema.builder().id(1L).name("CGV 강남").build();
        Product product = Product.builder().id(1L).name("고소팝콘").build();

        // 기존 재고는 5개밖에 없음
        Inventory existingInventory = Inventory.builder()
                .id(1L).cinema(cinema).product(product).stockQuantity(5).build();

        given(cinemaRepository.findById(1L)).willReturn(Optional.of(cinema));
        given(productRepository.findById(1L)).willReturn(Optional.of(product));
        given(inventoryRepository.findByCinemaIdAndProductId(1L, 1L)).willReturn(Optional.of(existingInventory));

        // When (실행) & Then (검증)
        CustomException exception = assertThrows(CustomException.class, () -> {
            inventoryService.updateInventory(request);
        });

        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.INVENTORY_SHORTAGE);
    }

    @Test
    @DisplayName("처음 입고되는 상품의 수량이 1 미만(0 또는 음수)이면 INVENTORY_SHORTAGE 예외가 발생한다")
    void updateInventory_Fail_NewShortage() {
        // Given (준비)
        InventoryUpdateRequest request = new InventoryUpdateRequest(1L, 1L, 0); // 0개 입고 요청

        Cinema cinema = Cinema.builder().id(1L).name("CGV 강남").build();
        Product product = Product.builder().id(1L).name("고소팝콘").build();

        given(cinemaRepository.findById(1L)).willReturn(Optional.of(cinema));
        given(productRepository.findById(1L)).willReturn(Optional.of(product));
        // 기존 재고가 아예 없다고 가정 (Optional.empty 반환)
        given(inventoryRepository.findByCinemaIdAndProductId(1L, 1L)).willReturn(Optional.empty());

        // When (실행) & Then (검증)
        CustomException exception = assertThrows(CustomException.class, () -> {
            inventoryService.updateInventory(request);
        });

        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.INVENTORY_SHORTAGE);
    }
}