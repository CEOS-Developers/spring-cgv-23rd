package com.ceos23.spring_boot.cgv.service.store;

import static org.assertj.core.api.Assertions.assertThat;

import com.ceos23.spring_boot.cgv.domain.cinema.Cinema;
import com.ceos23.spring_boot.cgv.domain.store.CinemaMenuStock;
import com.ceos23.spring_boot.cgv.domain.store.StoreMenu;
import com.ceos23.spring_boot.cgv.domain.store.StorePurchase;
import com.ceos23.spring_boot.cgv.domain.user.User;
import com.ceos23.spring_boot.cgv.domain.user.UserRole;
import com.ceos23.spring_boot.cgv.dto.store.StorePurchaseRequest;
import com.ceos23.spring_boot.cgv.repository.cinema.CinemaRepository;
import com.ceos23.spring_boot.cgv.repository.store.CinemaMenuStockRepository;
import com.ceos23.spring_boot.cgv.repository.store.StoreMenuRepository;
import com.ceos23.spring_boot.cgv.repository.store.StorePurchaseRepository;
import com.ceos23.spring_boot.cgv.repository.user.UserRepository;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class StoreQueryServiceTest {

    @Autowired
    private StoreQueryService storeQueryService;

    @Autowired
    private StorePurchaseService storePurchaseService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CinemaRepository cinemaRepository;

    @Autowired
    private StoreMenuRepository storeMenuRepository;

    @Autowired
    private CinemaMenuStockRepository cinemaMenuStockRepository;

    @Autowired
    private StorePurchaseRepository storePurchaseRepository;

    @AfterEach
    void tearDown() {
        storePurchaseRepository.deleteAllInBatch();
        cinemaMenuStockRepository.deleteAllInBatch();
        storeMenuRepository.deleteAllInBatch();
        cinemaRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();
    }

    @Test
    @DisplayName("store menus are returned for a cinema")
    void getStoreMenus_returnsCinemaStock() {
        StoreFixture fixture = createFixture();

        List<CinemaMenuStock> storeMenus = storeQueryService.getStoreMenus(fixture.cinema.getId());

        assertThat(storeMenus)
                .extracting(stock -> stock.getStoreMenu().getName())
                .containsExactly("Cola", "Popcorn");
    }

    @Test
    @DisplayName("purchase history returns the authenticated user's orders")
    void getPurchaseHistory_returnsPurchases() {
        StoreFixture fixture = createFixture();

        storePurchaseService.purchase(
                fixture.user.getId(),
                new StorePurchaseRequest(fixture.cinema.getId(), fixture.popcorn.getId(), 2)
        );

        List<StorePurchase> purchaseHistory = storeQueryService.getPurchaseHistory(fixture.user.getId());

        assertThat(purchaseHistory).hasSize(1);
        assertThat(purchaseHistory.getFirst().getTotalPrice()).isEqualTo(12_000);
        assertThat(purchaseHistory.getFirst().getCinemaMenuStock().getStoreMenu().getName())
                .isEqualTo("Popcorn");
    }

    private StoreFixture createFixture() {
        User user = userRepository.save(new User("user1", "user1@example.com", "password", UserRole.USER));
        Cinema cinema = cinemaRepository.save(new Cinema("CGV Gangnam", "Seoul"));
        StoreMenu cola = storeMenuRepository.save(new StoreMenu("Cola", 3000));
        StoreMenu popcorn = storeMenuRepository.save(new StoreMenu("Popcorn", 6000));
        cinemaMenuStockRepository.save(new CinemaMenuStock(20, cinema, cola));
        cinemaMenuStockRepository.save(new CinemaMenuStock(10, cinema, popcorn));

        return new StoreFixture(user, cinema, popcorn);
    }

    private record StoreFixture(User user, Cinema cinema, StoreMenu popcorn) {
    }
}
