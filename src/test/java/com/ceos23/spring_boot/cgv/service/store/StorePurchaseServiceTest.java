package com.ceos23.spring_boot.cgv.service.store;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ceos23.spring_boot.cgv.domain.cinema.Cinema;
import com.ceos23.spring_boot.cgv.domain.store.CinemaMenuStock;
import com.ceos23.spring_boot.cgv.domain.store.StoreMenu;
import com.ceos23.spring_boot.cgv.domain.store.StorePurchase;
import com.ceos23.spring_boot.cgv.domain.user.User;
import com.ceos23.spring_boot.cgv.domain.user.UserRole;
import com.ceos23.spring_boot.cgv.dto.store.StorePurchaseRequest;
import com.ceos23.spring_boot.cgv.global.exception.BadRequestException;
import com.ceos23.spring_boot.cgv.global.exception.NotFoundException;
import com.ceos23.spring_boot.cgv.repository.store.CinemaMenuStockRepository;
import com.ceos23.spring_boot.cgv.repository.store.StorePurchaseRepository;
import com.ceos23.spring_boot.cgv.repository.user.UserRepository;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class StorePurchaseServiceTest {

    @Mock
    private CinemaMenuStockRepository cinemaMenuStockRepository;

    @Mock
    private StorePurchaseRepository storePurchaseRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private StorePurchaseService storePurchaseService;

    @Test
    @DisplayName("매점 구매 성공 시 재고가 감소하고 구매 내역을 저장한다")
    void purchase_success() {
        User user = new User("지송", "jisong@example.com", "encoded-password", UserRole.USER);
        ReflectionTestUtils.setField(user, "id", 1L);

        Cinema cinema = new Cinema("CGV 강남", "서울시 강남구");
        ReflectionTestUtils.setField(cinema, "id", 1L);

        StoreMenu storeMenu = new StoreMenu("팝콘", 6000);
        ReflectionTestUtils.setField(storeMenu, "id", 1L);

        CinemaMenuStock stock = new CinemaMenuStock(10, cinema, storeMenu);
        ReflectionTestUtils.setField(stock, "id", 1L);

        StorePurchaseRequest request = new StorePurchaseRequest(1L, 1L, 3);

        given(userRepository.findById(1L)).willReturn(Optional.of(user));
        given(cinemaMenuStockRepository.findByCinemaIdAndStoreMenuId(1L, 1L))
                .willReturn(Optional.of(stock));

        storePurchaseService.purchase(1L, request);

        assertThat(stock.getStockQuantity()).isEqualTo(7);
        then(storePurchaseRepository).should().save(any(StorePurchase.class));
    }

    @Test
    @DisplayName("매점 구매 실패 - 재고 부족")
    void purchase_fail_insufficientStock() {
        User user = new User("지송", "jisong@example.com", "encoded-password", UserRole.USER);
        ReflectionTestUtils.setField(user, "id", 1L);

        Cinema cinema = new Cinema("CGV 강남", "서울시 강남구");
        StoreMenu storeMenu = new StoreMenu("팝콘", 6000);
        CinemaMenuStock stock = new CinemaMenuStock(2, cinema, storeMenu);

        StorePurchaseRequest request = new StorePurchaseRequest(1L, 1L, 3);

        given(userRepository.findById(1L)).willReturn(Optional.of(user));
        given(cinemaMenuStockRepository.findByCinemaIdAndStoreMenuId(1L, 1L))
                .willReturn(Optional.of(stock));

        assertThatThrownBy(() -> storePurchaseService.purchase(1L, request))
                .isInstanceOf(BadRequestException.class);
    }

    @Test
    @DisplayName("매점 구매 실패 - 재고 정보 없음")
    void purchase_fail_stockNotFound() {
        User user = new User("지송", "jisong@example.com", "encoded-password", UserRole.USER);
        ReflectionTestUtils.setField(user, "id", 1L);

        StorePurchaseRequest request = new StorePurchaseRequest(1L, 1L, 1);

        given(userRepository.findById(1L)).willReturn(Optional.of(user));
        given(cinemaMenuStockRepository.findByCinemaIdAndStoreMenuId(1L, 1L))
                .willReturn(Optional.empty());

        assertThatThrownBy(() -> storePurchaseService.purchase(1L, request))
                .isInstanceOf(NotFoundException.class);
    }
}
