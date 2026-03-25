package com.ceos23.cgv_clone.store.service;

import com.ceos23.cgv_clone.global.exception.CustomException;
import com.ceos23.cgv_clone.global.response.ErrorCode;
import com.ceos23.cgv_clone.store.domain.*;
import com.ceos23.cgv_clone.store.dto.request.OrderRequest;
import com.ceos23.cgv_clone.store.dto.request.OrderRequest.OrderItemRequest;
import com.ceos23.cgv_clone.store.dto.response.InventoryResponse;
import com.ceos23.cgv_clone.store.dto.response.OrderResponse;
import com.ceos23.cgv_clone.store.repository.InventoryRepository;
import com.ceos23.cgv_clone.store.repository.OrderRepository;
import com.ceos23.cgv_clone.store.repository.StoreRepository;
import com.ceos23.cgv_clone.user.domain.User;
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

        List<Inventory> inventories = new ArrayList<>();
        int totalPrice = 0;

        for (OrderItemRequest item : request.getItems()) {
            Inventory inventory = inventoryRepository.findById(item.getInventoryId())
                    .orElseThrow(() -> new CustomException(ErrorCode.ITEM_NOT_FOUND));

            // 다른 매점 재고 ID인지 확인
            if (!inventory.getStore().getId().equals(store.getId())) {
                throw new CustomException(ErrorCode.INVALID_INVENTORY);
            }

            inventory.decrease(item.getQuantity());
            totalPrice += inventory.getMenu().getPrice() * item.getQuantity();
            // 밑 반복문에서 사용하기 위해서 다시 넣기
            inventories.add(inventory);
        }

        Order order = Order.builder()
                .orderStatus(OrderStatus.PAID)
                .totalPrice(totalPrice)
                .user(user)
                .store(store)
                .build();

        orderRepository.save(order);

        List<OrderItemRequest> items = request.getItems();
        for (int i = 0; i < items.size(); i++) {
            OrderItem orderItem = OrderItem.builder()
                    .quantity(items.get(i).getQuantity())
                    .unitPrice(inventories.get(i).getMenu().getPrice())
                    .order(order)
                    .inventory(inventories.get(i))
                    .build();

            order.addOrderItem(orderItem);
        }

        return OrderResponse.from(order);
    }

    @Transactional(readOnly = true)
    public List<InventoryResponse> getInventories(Long storeId) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new CustomException(ErrorCode.STORE_NOT_FOUND));

        List<Inventory> response = inventoryRepository.findAllByStore_Id(storeId);

        return response.stream()
                .map(InventoryResponse::from)
                .toList();
    }
}
