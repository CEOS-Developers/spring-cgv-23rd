package com.ceos23.cgv_clone.store.service;

import com.ceos23.cgv_clone.global.exception.CustomException;
import com.ceos23.cgv_clone.global.response.ErrorCode;
import com.ceos23.cgv_clone.store.entity.*;
import com.ceos23.cgv_clone.store.dto.request.OrderRequest;
import com.ceos23.cgv_clone.store.dto.request.OrderRequest.OrderItemRequest;
import com.ceos23.cgv_clone.store.dto.response.InventoryResponse;
import com.ceos23.cgv_clone.store.dto.response.OrderResponse;
import com.ceos23.cgv_clone.store.repository.InventoryRepository;
import com.ceos23.cgv_clone.store.repository.OrderRepository;
import com.ceos23.cgv_clone.store.repository.StoreRepository;
import com.ceos23.cgv_clone.user.entity.User;
import com.ceos23.cgv_clone.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final UserRepository userRepository;
    private final StoreRepository storeRepository;
    private final InventoryRepository inventoryRepository;
    private final OrderRepository orderRepository;

    @Transactional
    public OrderResponse createOrder(Long userId, Long storeId, OrderRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new CustomException(ErrorCode.STORE_NOT_FOUND));
        List<OrderItemRequest> items = request.getItems();

        List<Inventory> inventories = validateAndDecreaseStock(store, items);
        int totalPrice = calculateTotalPrice(inventories, items);

        Order order = Order.builder()
                .orderStatus(OrderStatus.PAID)
                .totalPrice(totalPrice)
                .user(user)
                .store(store)
                .build();

        addOrderItems(order, inventories, items);
        orderRepository.save(order);

        return OrderResponse.from(order);
    }

    @Transactional(readOnly = true)
    public List<InventoryResponse> getInventories(Long storeId) {
        storeRepository.findById(storeId)
            .orElseThrow(() -> new CustomException(ErrorCode.STORE_NOT_FOUND));

        List<Inventory> response = inventoryRepository.findAllByStore_Id(storeId);

        return response.stream()
                .map(InventoryResponse::from)
                .toList();
    }

    private List<Inventory> validateAndDecreaseStock(Store store, List<OrderItemRequest> items) {
        List<Inventory> inventories = new ArrayList<>();

        for (OrderItemRequest item : items) {
            Inventory inventory = inventoryRepository.findById(item.getInventoryId())
                    .orElseThrow(() -> new CustomException(ErrorCode.ITEM_NOT_FOUND));

            // 다른 매점 재고 ID인지 확인
            if (!inventory.getStore().getId().equals(store.getId())) {
                throw new CustomException(ErrorCode.INVALID_INVENTORY);
            }

            inventory.decrease(item.getQuantity());
            inventories.add(inventory);
        }

        return inventories;
    }

    private int calculateTotalPrice(List<Inventory> inventories, List<OrderItemRequest> items) {
        int totalPrice = 0;
        for (int i = 0; i < items.size(); i++) {
            totalPrice += inventories.get(i).getMenu().getPrice() * items.get(i).getQuantity();
        }
        return totalPrice;
    }

    private void addOrderItems(Order order, List<Inventory> inventories, List<OrderItemRequest> items) {
        for (int i = 0; i < items.size(); i++) {
            order.addOrderItem(OrderItem.builder()
                    .quantity(items.get(i).getQuantity())
                    .unitPrice(inventories.get(i).getMenu().getPrice())
                    .order(order)
                    .inventory(inventories.get(i))
                    .build());
        }
    }
}
