package com.ceos23.spring_boot.service;

import com.ceos23.spring_boot.domain.Item;
import com.ceos23.spring_boot.domain.ItemOrder;
import com.ceos23.spring_boot.domain.OrderDetail;
import com.ceos23.spring_boot.domain.OrderStatus;
import com.ceos23.spring_boot.domain.Theater;
import com.ceos23.spring_boot.domain.TheaterItemStock;
import com.ceos23.spring_boot.domain.User;
import com.ceos23.spring_boot.dto.ItemOrderRequest;
import com.ceos23.spring_boot.dto.ItemOrderResponse;
import com.ceos23.spring_boot.dto.OrderItemRequest;
import com.ceos23.spring_boot.exception.CustomException;
import com.ceos23.spring_boot.global.exception.ErrorCode;
import com.ceos23.spring_boot.infra.payment.PaymentGateway;
import com.ceos23.spring_boot.infra.payment.dto.PaymentData;
import com.ceos23.spring_boot.repository.ItemOrderRepository;
import com.ceos23.spring_boot.repository.ItemRepository;
import com.ceos23.spring_boot.repository.TheaterItemStockRepository;
import com.ceos23.spring_boot.repository.TheaterRepository;
import com.ceos23.spring_boot.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemOrderService {

    private static final DateTimeFormatter PAYMENT_ID_FORMATTER =
            DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    private final ItemOrderRepository itemOrderRepository;
    private final TheaterItemStockRepository theaterItemStockRepository;
    private final UserRepository userRepository;
    private final TheaterRepository theaterRepository;
    private final ItemRepository itemRepository;
    private final PaymentGateway paymentGateway;

    @Transactional
    public ItemOrderResponse orderItems(ItemOrderRequest request) {
        validateRequest(request);

        User user = loadUser(request.getUserId());
        Theater theater = loadTheater(request.getTheaterId());

        List<Item> items = new ArrayList<>();
        List<TheaterItemStock> stocks = new ArrayList<>();
        int totalPrice = calculateTotalPriceAndPrepareOrderItems(request, items, stocks);

        ItemOrder itemOrder = createItemOrder(user, theater, totalPrice);
        ItemOrder savedOrder = itemOrderRepository.saveAndFlush(itemOrder);

        String paymentId = createPaymentId(savedOrder.getId());

        try {
            PaymentData payment = paymentGateway.pay(
                    paymentId,
                    createOrderName(savedOrder),
                    totalPrice,
                    createCustomData(savedOrder)
            );

            addOrderDetailsAndDecreaseStock(savedOrder, request.getItems(), items, stocks);
            savedOrder.markPaid(paymentId, resolvePaidAt(payment));

        } catch (Exception e) {
            savedOrder.markPaymentFailed(paymentId);
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
        validateUserExists(userId);

        return itemOrderRepository.findAllByUserId(userId).stream()
                .map(ItemOrderResponse::from)
                .toList();
    }

    @Transactional
    public ItemOrderResponse cancelOrder(Long orderId) {
        validateId(orderId);

        ItemOrder itemOrder = itemOrderRepository.findById(orderId)
                .orElseThrow(() -> new CustomException(ErrorCode.ITEM_ORDER_NOT_FOUND));

        if (itemOrder.getOrderStatus() != OrderStatus.PAID) {
            throw new IllegalArgumentException("결제 완료된 주문만 취소할 수 있습니다.");
        }

        paymentGateway.cancel(itemOrder.getPaymentId());

        restoreStocks(itemOrder);
        itemOrder.cancel(LocalDateTime.now());

        return ItemOrderResponse.from(itemOrder);
    }

    private int calculateTotalPriceAndPrepareOrderItems(
            ItemOrderRequest request,
            List<Item> items,
            List<TheaterItemStock> stocks
    ) {
        int totalPrice = 0;

        for (OrderItemRequest orderItemRequest : request.getItems()) {
            validateOrderItem(orderItemRequest);

            Item item = loadItem(orderItemRequest.getItemId());
            TheaterItemStock stock = loadStock(request.getTheaterId(), orderItemRequest.getItemId());

            stock.ensureEnough(orderItemRequest.getCount());

            items.add(item);
            stocks.add(stock);
            totalPrice += calculateItemPrice(item, orderItemRequest.getCount());
        }

        return totalPrice;
    }

    private void addOrderDetailsAndDecreaseStock(
            ItemOrder savedOrder,
            List<OrderItemRequest> orderItemRequests,
            List<Item> items,
            List<TheaterItemStock> stocks
    ) {
        for (int i = 0; i < orderItemRequests.size(); i++) {
            OrderItemRequest orderItemRequest = orderItemRequests.get(i);
            Item item = items.get(i);
            TheaterItemStock stock = stocks.get(i);

            stock.decreaseStock(orderItemRequest.getCount());
            savedOrder.addOrderDetail(item, orderItemRequest.getCount());
        }
    }

    private void restoreStocks(ItemOrder itemOrder) {
        Long theaterId = itemOrder.getTheater().getId();

        for (OrderDetail orderDetail : itemOrder.getOrderDetails()) {
            TheaterItemStock stock = loadStock(theaterId, orderDetail.getItem().getId());
            stock.increaseStock(orderDetail.getCount());
        }
    }

    private User loadUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
    }

    private Theater loadTheater(Long theaterId) {
        return theaterRepository.findById(theaterId)
                .orElseThrow(() -> new CustomException(ErrorCode.THEATER_NOT_FOUND));
    }

    private Item loadItem(Long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new CustomException(ErrorCode.ITEM_NOT_FOUND));
    }

    private TheaterItemStock loadStock(Long theaterId, Long itemId) {
        return theaterItemStockRepository.findWithLockByTheaterIdAndItemId(theaterId, itemId)
                .orElseThrow(() -> new CustomException(ErrorCode.ITEM_STOCK_NOT_FOUND));
    }

    private int calculateItemPrice(Item item, Integer count) {
        return item.getPrice() * count;
    }

    private ItemOrder createItemOrder(User user, Theater theater, int totalPrice) {
        return ItemOrder.of(user, theater, totalPrice);
    }

    private String createPaymentId(Long orderId) {
        return "ORD_" + orderId + "_" + LocalDateTime.now().format(PAYMENT_ID_FORMATTER);
    }

    private String createOrderName(ItemOrder itemOrder) {
        return "CGV 매점 주문 #" + itemOrder.getId();
    }

    private String createCustomData(ItemOrder itemOrder) {
        return "ITEM_ORDER:" + itemOrder.getId();
    }

    private LocalDateTime resolvePaidAt(PaymentData payment) {
        if (payment.getPaidAt() != null) {
            return payment.getPaidAt();
        }

        return LocalDateTime.now();
    }

    private void validateUserExists(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new CustomException(ErrorCode.USER_NOT_FOUND);
        }
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