package com.ceos23.spring_boot.domain.store.service;

import com.ceos23.spring_boot.domain.store.dto.OrderCommand;
import com.ceos23.spring_boot.domain.store.dto.OrderInfo;
import com.ceos23.spring_boot.domain.store.dto.OrderItemCommand;
import com.ceos23.spring_boot.domain.store.entity.Inventory;
import com.ceos23.spring_boot.domain.store.entity.Menu;
import com.ceos23.spring_boot.domain.store.entity.Order;
import com.ceos23.spring_boot.domain.store.entity.OrderItem;
import com.ceos23.spring_boot.domain.store.repository.InventoryRepository;
import com.ceos23.spring_boot.domain.store.repository.MenuRepository;
import com.ceos23.spring_boot.domain.store.repository.OrderItemRepository;
import com.ceos23.spring_boot.domain.store.repository.OrderRepository;
import com.ceos23.spring_boot.domain.theater.entity.Theater;
import com.ceos23.spring_boot.domain.theater.repository.TheaterRepository;
import com.ceos23.spring_boot.domain.user.entity.User;
import com.ceos23.spring_boot.domain.user.repository.UserRepository;
import com.ceos23.spring_boot.global.exception.BusinessException;
import com.ceos23.spring_boot.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final UserRepository userRepository;
    private final TheaterRepository theaterRepository;
    private final MenuRepository menuRepository;
    private final InventoryRepository inventoryRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;

    @Transactional
    public OrderInfo createOrder(OrderCommand command) {
        User user = userRepository.findById(command.userId())
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        Theater theater = theaterRepository.findById(command.theaterId())
                .orElseThrow(() -> new BusinessException(ErrorCode.THEATER_NOT_FOUND));

        int totalPrice = 0;
        List<OrderItem> orderItems = new ArrayList<>();

        for (OrderItemCommand itemCommand : command.orderItems()) {
            Menu menu = menuRepository.findById(itemCommand.menuId())
                    .orElseThrow(() -> new BusinessException(ErrorCode.MENU_NOT_FOUND));

            Inventory inventory = inventoryRepository.findByTheaterIdAndMenuIdWithLock(theater.getId(), menu.getId())
                    .orElseThrow(() -> new BusinessException(ErrorCode.INVENTORY_NOT_FOUND));

            inventory.decreaseStock(itemCommand.count());

            int currentOrderPrice = menu.getPrice();
            totalPrice += (currentOrderPrice * itemCommand.count());

            OrderItem orderItem = OrderItem.builder()
                    .menu(menu)
                    .orderPrice(currentOrderPrice)
                    .count(itemCommand.count())
                    .build();
            orderItems.add(orderItem);
        }

        Order order = Order.builder()
                .user(user)
                .theater(theater)
                .totalPrice(totalPrice)
                .refundable(false)
                .build();
        orderRepository.save(order);

        for (OrderItem orderItem : orderItems) {
            orderItem.updateOrder(order);
        }
        orderItemRepository.saveAll(orderItems);

        return OrderInfo.from(order, orderItems);
    }
}