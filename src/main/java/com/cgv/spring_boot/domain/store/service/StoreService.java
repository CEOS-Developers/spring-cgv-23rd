package com.cgv.spring_boot.domain.store.service;

import com.cgv.spring_boot.domain.store.dto.request.StoreOrderRequest;
import com.cgv.spring_boot.domain.store.entity.Item;
import com.cgv.spring_boot.domain.store.entity.OrderItem;
import com.cgv.spring_boot.domain.store.entity.StoreInventory;
import com.cgv.spring_boot.domain.store.entity.StoreOrder;
import com.cgv.spring_boot.domain.store.repository.ItemRepository;
import com.cgv.spring_boot.domain.store.repository.OrderItemRepository;
import com.cgv.spring_boot.domain.store.repository.StoreInventoryRepository;
import com.cgv.spring_boot.domain.store.repository.StoreOrderRepository;
import com.cgv.spring_boot.domain.theater.entity.Theater;
import com.cgv.spring_boot.domain.theater.repository.TheaterRepository;
import com.cgv.spring_boot.domain.user.entity.User;
import com.cgv.spring_boot.domain.user.repository.UserRepository;
import com.cgv.spring_boot.domain.store.exception.StoreErrorCode;
import com.cgv.spring_boot.domain.theater.exception.TheaterErrorCode;
import com.cgv.spring_boot.domain.user.exception.UserErrorCode;
import com.cgv.spring_boot.global.error.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class StoreService {

    private final UserRepository userRepository;
    private final TheaterRepository theaterRepository;
    private final ItemRepository itemRepository;
    private final StoreInventoryRepository storeInventoryRepository;
    private final StoreOrderRepository storeOrderRepository;
    private final OrderItemRepository orderItemRepository;

    /**
     * 매점 주문 메서드
     */
    @Transactional
    public Long order(Long userId, StoreOrderRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(UserErrorCode.USER_NOT_FOUND));

        Theater theater = theaterRepository.findById(request.theaterId())
                .orElseThrow(() -> new BusinessException(TheaterErrorCode.THEATER_NOT_FOUND));

        StoreOrder storeOrder = StoreOrder.builder()
                .totalPrice(0)
                .orderDate(LocalDateTime.now())
                .user(user)
                .theater(theater)
                .build();

        List<OrderItem> orderItems = new ArrayList<>();

        for (StoreOrderRequest.OrderItemRequest itemRequest : request.items()) {
            Item item = itemRepository.findById(itemRequest.itemId())
                    .orElseThrow(() -> new BusinessException(StoreErrorCode.ITEM_NOT_FOUND));

            // 재고 조회
            StoreInventory inventory = storeInventoryRepository.findByTheaterIdAndItemId(request.theaterId(), itemRequest.itemId())
                    .orElseThrow(() -> new BusinessException(StoreErrorCode.STORE_INVENTORY_NOT_FOUND));

            inventory.decreaseStock(itemRequest.count());
            orderItems.add(OrderItem.create(storeOrder, item, itemRequest.count()));
        }

        int totalPrice = orderItems.stream()
                .mapToInt(OrderItem::getTotalPrice)
                .sum();

        storeOrder.updateTotalPrice(totalPrice);

        StoreOrder savedOrder = storeOrderRepository.save(storeOrder);

        orderItemRepository.saveAll(orderItems);

        return savedOrder.getId();
    }
}
