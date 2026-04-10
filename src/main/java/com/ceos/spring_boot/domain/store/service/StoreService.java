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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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

    public OrderResponse createOrder(Long userId, OrderRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException(ErrorCode.USER_NOT_FOUND_ERROR.getMessage()));

        Cinema cinema = cinemaRepository.findById(request.cinemaId())
                .orElseThrow(() -> new IllegalArgumentException(ErrorCode.CINEMA_NOT_FOUND_ERROR.getMessage()));

        Order order = Order.builder()
                .user(user)
                .cinema(cinema)
                .status(OrderStatus.COMPLETED)
                .totalPrice(0)
                .build();

        int totalPrice = 0;

        // 주문 아이템 처리 및 재고 차감
        for (OrderItemRequest itemRequest : request.orderItems()) {
            Product product = productRepository.findById(itemRequest.productId())
                    .orElseThrow(() -> new IllegalArgumentException(ErrorCode.PRODUCT_NOT_FOUND_ERROR.getMessage()));

            // 해당 지점의 재고 조회
            Stock stock = stockRepository.findByCinemaIdAndProductId(cinema.getId(), product.getId())
                    .orElseThrow(() -> new IllegalArgumentException(ErrorCode.STOCK_NOT_FOUND_ERROR.getMessage()));

            // 재고 수량 검증 및 차감
            if (stock.getQuantity() < itemRequest.count()) {
                throw new IllegalStateException(ErrorCode.OUT_OF_STOCK_ERROR.getMessage());
            }
            stock.decreaseQuantity(itemRequest.count()); // 재고 감소

            OrderItem orderItem = OrderItem.builder()
                    .order(order)
                    .product(product)
                    .count(itemRequest.count())
                    .build();

            order.addOrderItem(orderItem);
            totalPrice += product.getPrice() * itemRequest.count();
        }

        order.updateTotalPrice(totalPrice); // 최종 금액 업데이트
        orderRepository.save(order);

        return OrderResponse.from(order);
    }

    @Transactional(readOnly = true)
    public List<OrderItemResponse> getOrderItems(Long userId, Long orderId) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException(ErrorCode.ORDER_NOT_FOUND_ERROR.getMessage()));

        if (!order.getUser().getId().equals(userId)) {
            throw new IllegalStateException(ErrorCode.ORDER_NOT_MINE.getMessage());
        }

        // 해당 주문의 아이템들 조회
        List<OrderItem> items = orderItemRepository.findAllByOrderId(orderId);

        return items.stream()
                .map(OrderItemResponse::from)
                .toList();
    }
}
