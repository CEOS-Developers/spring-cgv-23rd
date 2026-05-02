package com.ceos23.spring_boot.service;

import com.ceos23.spring_boot.domain.Item;
import com.ceos23.spring_boot.domain.ItemOrder;
import com.ceos23.spring_boot.domain.OrderDetail;
import com.ceos23.spring_boot.domain.Theater;
import com.ceos23.spring_boot.domain.TheaterItemStock;
import com.ceos23.spring_boot.domain.User;
import com.ceos23.spring_boot.dto.ItemOrderRequest;
import com.ceos23.spring_boot.dto.OrderItemRequest;
import com.ceos23.spring_boot.exception.CustomException;
import com.ceos23.spring_boot.global.exception.ErrorCode;
import com.ceos23.spring_boot.repository.ItemOrderRepository;
import com.ceos23.spring_boot.repository.ItemRepository;
import com.ceos23.spring_boot.repository.TheaterItemStockRepository;
import com.ceos23.spring_boot.repository.TheaterRepository;
import com.ceos23.spring_boot.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.CannotAcquireLockException;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.PessimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemOrderTransactionService {

    private final ItemOrderRepository itemOrderRepository;
    private final TheaterItemStockRepository theaterItemStockRepository;
    private final UserRepository userRepository;
    private final TheaterRepository theaterRepository;
    private final ItemRepository itemRepository;

    @Transactional
    public ItemOrder createOrderAndDecreaseStock(ItemOrderRequest request) {
        try {
            User user = loadUser(request.getUserId());
            Theater theater = loadTheater(request.getTheaterId());

            int totalPrice = calculateTotalPrice(request);

            ItemOrder itemOrder = ItemOrder.of(user, theater, totalPrice);
            ItemOrder savedOrder = itemOrderRepository.saveAndFlush(itemOrder);

            addOrderDetailsAndDecreaseStock(savedOrder, request);

            return savedOrder;

        } catch (CannotAcquireLockException | PessimisticLockingFailureException e) {
            throw new CustomException(ErrorCode.ITEM_ORDER_LOCK_FAILED);

        } catch (DataAccessException e) {
            throw new CustomException(ErrorCode.ITEM_ORDER_DB_ERROR);
        }
    }

    @Transactional
    public ItemOrder markPaid(Long orderId, String paymentId, LocalDateTime paidAt) {
        ItemOrder itemOrder = loadOrder(orderId);
        itemOrder.markPaid(paymentId, paidAt);
        return itemOrder;
    }

    @Transactional
    public void markPaymentFailedAndRestoreStock(Long orderId, String paymentId) {
        ItemOrder itemOrder = loadOrder(orderId);

        restoreStocks(itemOrder);
        itemOrder.markPaymentFailed(paymentId);
    }

    @Transactional
    public ItemOrder cancelOrder(Long orderId) {
        ItemOrder itemOrder = loadOrder(orderId);

        restoreStocks(itemOrder);
        itemOrder.cancel(LocalDateTime.now());

        return itemOrder;
    }

    private int calculateTotalPrice(ItemOrderRequest request) {
        int totalPrice = 0;

        for (OrderItemRequest orderItemRequest : request.getItems()) {
            Item item = loadItem(orderItemRequest.getItemId());
            totalPrice += item.getPrice() * orderItemRequest.getCount();
        }

        return totalPrice;
    }

    private void addOrderDetailsAndDecreaseStock(ItemOrder savedOrder, ItemOrderRequest request) {
        List<OrderItemRequest> sortedOrderItems = request.getItems().stream()
                .sorted(Comparator.comparing(OrderItemRequest::getItemId))
                .toList();

        List<Long> itemIds = sortedOrderItems.stream()
                .map(OrderItemRequest::getItemId)
                .toList();

        Map<Long, TheaterItemStock> stockMap = theaterItemStockRepository
                .findAllWithLockByTheaterIdAndItemIdsOrderByItemId(request.getTheaterId(), itemIds)
                .stream()
                .collect(Collectors.toMap(
                        stock -> stock.getItem().getId(),
                        Function.identity()
                ));

        for (OrderItemRequest orderItemRequest : sortedOrderItems) {
            Item item = loadItem(orderItemRequest.getItemId());
            TheaterItemStock stock = stockMap.get(orderItemRequest.getItemId());

            if (stock == null) {
                throw new CustomException(ErrorCode.ITEM_STOCK_NOT_FOUND);
            }

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

    private ItemOrder loadOrder(Long orderId) {
        return itemOrderRepository.findById(orderId)
                .orElseThrow(() -> new CustomException(ErrorCode.ITEM_ORDER_NOT_FOUND));
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
}