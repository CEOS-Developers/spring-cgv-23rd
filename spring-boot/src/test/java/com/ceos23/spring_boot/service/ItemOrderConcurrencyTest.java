package com.ceos23.spring_boot.service;

import com.ceos23.spring_boot.domain.Item;
import com.ceos23.spring_boot.domain.Theater;
import com.ceos23.spring_boot.domain.TheaterItemStock;
import com.ceos23.spring_boot.domain.User;
import com.ceos23.spring_boot.dto.ItemOrderRequest;
import com.ceos23.spring_boot.dto.OrderItemRequest;
import com.ceos23.spring_boot.infra.payment.PaymentGateway;
import com.ceos23.spring_boot.infra.payment.dto.PaymentData;
import com.ceos23.spring_boot.repository.ItemRepository;
import com.ceos23.spring_boot.repository.TheaterItemStockRepository;
import com.ceos23.spring_boot.repository.TheaterRepository;
import com.ceos23.spring_boot.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest
class ItemOrderConcurrencyTest {

    @Autowired
    private ItemOrderService itemOrderService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TheaterRepository theaterRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private TheaterItemStockRepository theaterItemStockRepository;

    @MockBean
    private PaymentGateway paymentGateway;

    @Test
    @DisplayName("재고가 1개일 때 동시에 2번 주문하면 1번만 성공한다")
    void orderItemConcurrencyTest() throws InterruptedException {
        PaymentData paymentData = mock(PaymentData.class);
        when(paymentData.getPaidAt()).thenReturn(LocalDateTime.now());

        BDDMockito.given(paymentGateway.pay(anyString(), anyString(), anyInt(), anyString()))
                .willReturn(paymentData);

        User user1 = userRepository.save(User.of("user1", "1234"));
        User user2 = userRepository.save(User.of("user2", "1234"));
        Theater theater = theaterRepository.save(new Theater("강남 CGV", "서울"));
        Item item = itemRepository.save(Item.of("팝콘", 5000));

        theaterItemStockRepository.save(TheaterItemStock.of(theater, item, 1));

        int threadCount = 2;
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch readyLatch = new CountDownLatch(threadCount);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch doneLatch = new CountDownLatch(threadCount);

        AtomicInteger successCount = new AtomicInteger();
        AtomicInteger failCount = new AtomicInteger();

        List<Long> userIds = List.of(user1.getId(), user2.getId());

        for (Long userId : userIds) {
            executorService.submit(() -> {
                try {
                    readyLatch.countDown();
                    startLatch.await();

                    ItemOrderRequest request = new ItemOrderRequest(
                            userId,
                            theater.getId(),
                            List.of(new OrderItemRequest(item.getId(), 1))
                    );

                    itemOrderService.orderItems(request);
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    failCount.incrementAndGet();
                } finally {
                    doneLatch.countDown();
                }
            });
        }

        readyLatch.await();
        startLatch.countDown();
        doneLatch.await();
        executorService.shutdown();

        TheaterItemStock stock = theaterItemStockRepository
                .findByTheaterIdAndItemId(theater.getId(), item.getId())
                .orElseThrow();

        assertThat(successCount.get()).isEqualTo(1);
        assertThat(failCount.get()).isEqualTo(1);
        assertThat(stock.getStock()).isEqualTo(0);
    }
}