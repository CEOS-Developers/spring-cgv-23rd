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
import com.cgv.spring_boot.global.common.code.ErrorCode;
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

    @Transactional
    public Long order(Long userId, StoreOrderRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        Theater theater = theaterRepository.findById(request.theaterId())
                .orElseThrow(() -> new BusinessException(ErrorCode.THEATER_NOT_FOUND));

        List<PreparedOrderItem> preparedOrderItems = new ArrayList<>();
        int totalPrice = 0;

        for (StoreOrderRequest.OrderItemRequest itemRequest : request.items()) {
            Item item = itemRepository.findById(itemRequest.itemId())
                    .orElseThrow(() -> new BusinessException(ErrorCode.ITEM_NOT_FOUND));

            StoreInventory inventory = storeInventoryRepository.findByTheaterIdAndItemId(request.theaterId(), itemRequest.itemId())
                    .orElseThrow(() -> new BusinessException(ErrorCode.STORE_INVENTORY_NOT_FOUND));

            inventory.decreaseStock(itemRequest.count());

            preparedOrderItems.add(new PreparedOrderItem(item, item.getPrice(), itemRequest.count()));
            totalPrice += item.getPrice() * itemRequest.count();
        }

        StoreOrder storeOrder = StoreOrder.builder()
                .totalPrice(totalPrice)
                .orderDate(LocalDateTime.now())
                .user(user)
                .theater(theater)
                .build();

        StoreOrder savedOrder = storeOrderRepository.save(storeOrder);

        List<OrderItem> orderItems = preparedOrderItems.stream()
                .map(preparedOrderItem -> OrderItem.builder()
                        .order(savedOrder)
                        .item(preparedOrderItem.item())
                        .orderPrice(preparedOrderItem.orderPrice())
                        .count(preparedOrderItem.count())
                        .build())
                .toList();

        orderItemRepository.saveAll(orderItems);

        return savedOrder.getId();
    }

    private record PreparedOrderItem(
            Item item,
            int orderPrice,
            int count
    ) {
    }
}
