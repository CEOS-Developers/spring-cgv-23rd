package com.ceos.spring_boot.domain.store.service;

import com.ceos.spring_boot.domain.cinema.entity.Cinema;
import com.ceos.spring_boot.domain.cinema.repository.CinemaRepository;
import com.ceos.spring_boot.domain.store.dto.OrderItemRequest;
import com.ceos.spring_boot.domain.store.dto.OrderItemResponse;
import com.ceos.spring_boot.domain.store.dto.OrderRequest;
import com.ceos.spring_boot.domain.store.dto.OrderResponse;
import com.ceos.spring_boot.domain.store.entity.*;
import com.ceos.spring_boot.domain.store.repository.OrderItemRepository;
import com.ceos.spring_boot.domain.store.repository.OrderRepository;
import com.ceos.spring_boot.domain.store.repository.ProductRepository;
import com.ceos.spring_boot.domain.store.repository.StockRepository;
import com.ceos.spring_boot.domain.user.entity.User;
import com.ceos.spring_boot.domain.user.repository.UserRepository;
import com.ceos.spring_boot.global.codes.ErrorCode;
import com.ceos.spring_boot.global.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Transactional
public class StoreService {

    private final OrderRepository orderRepository;
    private final StockRepository stockRepository;
    private final UserRepository userRepository;
    private final CinemaRepository cinemaRepository;
    private final ProductRepository productRepository;
    private final OrderItemRepository orderItemRepository;

    private final RedissonClient redissonClient;
    private final TransactionTemplate transactionTemplate;

    public OrderResponse createOrder(Long userId, OrderRequest request) { // N+1 문제 해결해야한다..

        List<String> lockKeys = request.orderItems().stream()
                .map(item -> "lock:stock:" + request.cinemaId() + ":" + item.productId())
                .sorted()
                .toList();

        // MultiLock 설정
        RLock multiLock = redissonClient.getMultiLock(
                lockKeys.stream().map(redissonClient::getLock).toArray(RLock[]::new)
        );


        try {
            // 락 획득 시도 (최대 5초 대기, 3초 유지)
            boolean isLocked = multiLock.tryLock(5, 3, TimeUnit.SECONDS);
            if (!isLocked) {
                throw new BusinessException(ErrorCode.CONFLICT_ERROR); // 또는 적절한 에러코드
            }

            // 트랜잭션 내에서 비즈니스 로직 수행
            return transactionTemplate.execute(status -> {
                // 데이터 조회가 필요 없는 객체는 프록시로 가져옴
                User user = userRepository.getReferenceById(userId);
                Cinema cinema = cinemaRepository.getReferenceById(request.cinemaId());

                Order order = Order.create(user, cinema);

                int totalPrice = 0;
                for (OrderItemRequest itemRequest : request.orderItems()) {

                    Product product = productRepository.findById(itemRequest.productId())
                            .orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_NOT_FOUND_ERROR));

                    Stock stock = stockRepository.findByCinemaIdAndProductId(cinema.getId(), product.getId())
                            .orElseThrow(() -> new BusinessException(ErrorCode.STOCK_NOT_FOUND_ERROR));

                    if (stock.getQuantity() < itemRequest.count()) {
                        throw new BusinessException(ErrorCode.OUT_OF_STOCK_ERROR);
                    }
                    stock.decreaseQuantity(itemRequest.count());

                    OrderItem.create(order, product, itemRequest.count());

                    totalPrice += product.getPrice() * itemRequest.count();
                }

                order.updateTotalPrice(totalPrice);
                return OrderResponse.from(orderRepository.save(order));
            });

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR);
        } finally {
            // 5. 트랜잭션 종료 후 안전하게 락 해제
            if (multiLock.isHeldByCurrentThread()) {
                multiLock.unlock();
            }
        }
    }

    @Transactional(readOnly = true)
    public List<OrderItemResponse> getOrderItems(Long userId, Long orderId) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ORDER_NOT_FOUND_ERROR));

        if (!order.getUser().getId().equals(userId)) {
            throw new BusinessException(ErrorCode.ORDER_NOT_MINE);
        }

        // 해당 주문의 아이템들 조회
        List<OrderItem> items = orderItemRepository.findAllByOrderId(orderId);

        return items.stream()
                .map(OrderItemResponse::from)
                .toList();
    }

    // 재고 복구 로직
    @Transactional
    public void restoreStock(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ORDER_NOT_FOUND_ERROR));

        // 주문에 포함된 아이템들을 돌면서 재고 증가
        for (OrderItem item : order.getOrderItems()) {
            Stock stock = stockRepository.findByCinemaIdAndProductId(
                    order.getCinema().getId(),
                    item.getProduct().getId()
            ).orElseThrow(() -> new BusinessException(ErrorCode.STOCK_NOT_FOUND_ERROR));

            stock.increaseQuantity(item.getCount()); // Stock 엔티티에 increaseQuantity 구현 필요
        }

        order.cancel(); // Order 엔티티에 상태를 CANCELLED로 바꾸는 메서드 추가 필요
    }
}
