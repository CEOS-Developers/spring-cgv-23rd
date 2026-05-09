package com.ceos23.spring_boot.cgv.service.store;

import static org.assertj.core.api.Assertions.assertThat;

import com.ceos23.spring_boot.cgv.domain.cinema.Cinema;
import com.ceos23.spring_boot.cgv.domain.store.CinemaMenuStock;
import com.ceos23.spring_boot.cgv.domain.store.StoreMenu;
import com.ceos23.spring_boot.cgv.domain.store.StorePurchase;
import com.ceos23.spring_boot.cgv.domain.user.User;
import com.ceos23.spring_boot.cgv.domain.user.UserRole;
import com.ceos23.spring_boot.cgv.dto.store.StorePurchaseRequest;
import com.ceos23.spring_boot.cgv.global.exception.BadRequestException;
import com.ceos23.spring_boot.cgv.global.exception.ErrorCode;
import com.ceos23.spring_boot.cgv.repository.cinema.CinemaRepository;
import com.ceos23.spring_boot.cgv.repository.store.CinemaMenuStockRepository;
import com.ceos23.spring_boot.cgv.repository.store.StoreMenuRepository;
import com.ceos23.spring_boot.cgv.repository.store.StorePurchaseRepository;
import com.ceos23.spring_boot.cgv.repository.user.UserRepository;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class StorePurchaseConcurrencyTest {

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
    @DisplayName("only one purchase can consume the last item at the same time")
    void purchase_allowsOnlyOneWinnerForLastStock() throws Exception {
        User user1 = userRepository.save(new User("user1", "user1@example.com", "password", UserRole.USER));
        User user2 = userRepository.save(new User("user2", "user2@example.com", "password", UserRole.USER));
        Cinema cinema = cinemaRepository.save(new Cinema("CGV Gangnam", "Seoul"));
        StoreMenu menu = storeMenuRepository.save(new StoreMenu("Popcorn", 6000));
        CinemaMenuStock stock = cinemaMenuStockRepository.save(new CinemaMenuStock(1, cinema, menu));
        StorePurchaseRequest request = new StorePurchaseRequest(cinema.getId(), menu.getId(), 1);

        ExecutorService executorService = Executors.newFixedThreadPool(2);
        CountDownLatch readyLatch = new CountDownLatch(2);
        CountDownLatch startLatch = new CountDownLatch(1);
        AtomicInteger successCount = new AtomicInteger();
        AtomicInteger insufficientStockCount = new AtomicInteger();

        Future<Void> firstRequest = executorService.submit(
                purchaseTask(user1.getId(), request, readyLatch, startLatch, successCount, insufficientStockCount)
        );
        Future<Void> secondRequest = executorService.submit(
                purchaseTask(user2.getId(), request, readyLatch, startLatch, successCount, insufficientStockCount)
        );

        assertThat(readyLatch.await(5, TimeUnit.SECONDS)).isTrue();
        startLatch.countDown();

        waitFor(firstRequest);
        waitFor(secondRequest);
        executorService.shutdown();

        List<StorePurchase> purchases = storePurchaseRepository.findAll();
        CinemaMenuStock updatedStock = cinemaMenuStockRepository.findById(stock.getId()).orElseThrow();

        assertThat(successCount.get()).isEqualTo(1);
        assertThat(insufficientStockCount.get()).isEqualTo(1);
        assertThat(purchases).hasSize(1);
        assertThat(updatedStock.getStockQuantity()).isZero();
    }

    private Callable<Void> purchaseTask(
            Long userId,
            StorePurchaseRequest request,
            CountDownLatch readyLatch,
            CountDownLatch startLatch,
            AtomicInteger successCount,
            AtomicInteger insufficientStockCount
    ) {
        return () -> {
            readyLatch.countDown();
            assertThat(startLatch.await(5, TimeUnit.SECONDS)).isTrue();

            try {
                storePurchaseService.purchase(userId, request);
                successCount.incrementAndGet();
            } catch (BadRequestException exception) {
                assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.INSUFFICIENT_MENU_STOCK);
                insufficientStockCount.incrementAndGet();
            }

            return null;
        };
    }

    private void waitFor(Future<Void> future) throws Exception {
        try {
            future.get(5, TimeUnit.SECONDS);
        } catch (ExecutionException exception) {
            throw new RuntimeException(exception.getCause());
        }
    }
}
