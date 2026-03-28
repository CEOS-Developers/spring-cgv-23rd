package com.ceos23.spring_boot.service;

import com.ceos23.spring_boot.domain.*;
import com.ceos23.spring_boot.dto.ItemOrderRequest;
import com.ceos23.spring_boot.dto.ItemOrderResponse;
import com.ceos23.spring_boot.dto.OrderItemRequest;
import com.ceos23.spring_boot.exception.CustomException;
import com.ceos23.spring_boot.global.exception.ErrorCode;
import com.ceos23.spring_boot.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemOrderService {

    private final ItemOrderRepository itemOrderRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final TheaterItemStockRepository theaterItemStockRepository;
    private final UserRepository userRepository;
    private final TheaterRepository theaterRepository;
    private final ItemRepository itemRepository;

    @Transactional
    public ItemOrderResponse orderItems(ItemOrderRequest request) {
        validateRequest(request);

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        Theater theater = theaterRepository.findById(request.getTheaterId())
                .orElseThrow(() -> new CustomException(ErrorCode.THEATER_NOT_FOUND));

        List<TheaterItemStock> stocks = new ArrayList<>();
        List<Item> items = new ArrayList<>();
        int totalPrice = 0;

        for (OrderItemRequest orderItemRequest : request.getItems()) {
            validateOrderItem(orderItemRequest);

            Item item = itemRepository.findById(orderItemRequest.getItemId())
                    .orElseThrow(() -> new CustomException(ErrorCode.ITEM_NOT_FOUND));

            TheaterItemStock stock = theaterItemStockRepository
                    .findByTheaterIdAndItemId(request.getTheaterId(), orderItemRequest.getItemId())
                    .orElseThrow(() -> new CustomException(ErrorCode.ITEM_STOCK_NOT_FOUND));

            if (stock.getStock() < orderItemRequest.getCount()) {
                throw new CustomException(ErrorCode.INSUFFICIENT_STOCK);
            }

            items.add(item);
            stocks.add(stock);
            totalPrice += item.getPrice() * orderItemRequest.getCount();
        }

        ItemOrder itemOrder = ItemOrder.of(user, theater, totalPrice, LocalDateTime.now());
        ItemOrder savedOrder = itemOrderRepository.save(itemOrder);

        for (int i = 0; i < request.getItems().size(); i++) {
            OrderItemRequest orderItemRequest = request.getItems().get(i);
            Item item = items.get(i);
            TheaterItemStock stock = stocks.get(i);

            stock.decreaseStock(orderItemRequest.getCount());

            OrderDetail orderDetail = OrderDetail.of(savedOrder, item, orderItemRequest.getCount());
            savedOrder.addOrderDetail(orderDetail);
            orderDetailRepository.save(orderDetail);
        }

        return ItemOrderResponse.from(savedOrder);
    }

    public ItemOrderResponse getOrder(Long orderId) {
        validateId(orderId);

        ItemOrder itemOrder = itemOrderRepository.findById(orderId)
                .orElseThrow(() -> new CustomException(ErrorCode.ITEM_ORDER_NOT_FOUND));

        return ItemOrderResponse.from(itemOrder);
    }

    public List<ItemOrderResponse> getOrdersByUser(Long userId) {
        validateId(userId);

        if (!userRepository.existsById(userId)) {
            throw new CustomException(ErrorCode.USER_NOT_FOUND);
        }

        return itemOrderRepository.findAllByUserId(userId).stream()
                .map(ItemOrderResponse::from)
                .toList();
    }

    private void validateRequest(ItemOrderRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("주문 요청은 비어 있을 수 없습니다.");
        }

        validateId(request.getUserId());
        validateId(request.getTheaterId());

        if (request.getItems() == null || request.getItems().isEmpty()) {
            throw new IllegalArgumentException("주문 상품은 1개 이상이어야 합니다.");
        }
    }

    private void validateOrderItem(OrderItemRequest orderItemRequest) {
        if (orderItemRequest == null) {
            throw new IllegalArgumentException("주문 상품 정보가 비어 있습니다.");
        }

        validateId(orderItemRequest.getItemId());

        if (orderItemRequest.getCount() == null || orderItemRequest.getCount() <= 0) {
            throw new IllegalArgumentException("상품 수량은 1 이상이어야 합니다.");
        }
    }

    private void validateId(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("id는 1 이상이어야 합니다.");
        }
    }
}