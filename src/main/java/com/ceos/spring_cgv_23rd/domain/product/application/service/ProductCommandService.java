package com.ceos.spring_cgv_23rd.domain.product.application.service;

import com.ceos.spring_cgv_23rd.domain.product.application.dto.command.CreateOrderCommand;
import com.ceos.spring_cgv_23rd.domain.product.application.dto.result.OrderDetailResult;
import com.ceos.spring_cgv_23rd.domain.product.application.port.in.CancelOrderUseCase;
import com.ceos.spring_cgv_23rd.domain.product.application.port.in.CreateOrderUseCase;
import com.ceos.spring_cgv_23rd.domain.product.application.port.out.ProductPersistencePort;
import com.ceos.spring_cgv_23rd.domain.product.domain.Inventory;
import com.ceos.spring_cgv_23rd.domain.product.domain.OrderItem;
import com.ceos.spring_cgv_23rd.domain.product.domain.Product;
import com.ceos.spring_cgv_23rd.domain.product.domain.ProductOrder;
import com.ceos.spring_cgv_23rd.domain.product.exception.ProductErrorCode;
import com.ceos.spring_cgv_23rd.domain.theater.exception.TheaterErrorCode;
import com.ceos.spring_cgv_23rd.global.apiPayload.exception.GeneralException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductCommandService implements CreateOrderUseCase, CancelOrderUseCase {

    private final ProductPersistencePort productPersistencePort;

    @Override
    @Transactional
    public OrderDetailResult createOrder(Long userId, CreateOrderCommand command) {

        // 영화관명 조회
        String theaterName = productPersistencePort.findTheaterNameById(command.theaterId())
                .orElseThrow(() -> new GeneralException(TheaterErrorCode.THEATER_NOT_FOUND));

        // 요청한 상품 ID 목록 추출
        List<Long> productIds = command.items().stream()
                .map(CreateOrderCommand.OrderItemCommand::productId)
                .toList();

        // 상품 일괄 조회
        Map<Long, Product> productMap = findProductsOrThrow(productIds);

        // 재고 차감
        decreaseInventories(command.theaterId(), command.items());

        // 주문 아이템 생성
        List<OrderItem> orderItems = command.items().stream()
                .map(item -> {
                    Product product = productMap.get(item.productId());
                    return OrderItem.createOrderItem(product.getId(), product.getPrice(), item.quantity());
                })
                .toList();

        // 주문 생성 + 저장
        ProductOrder order = ProductOrder.createOrder(userId, command.theaterId(), orderItems);
        ProductOrder savedOrder = productPersistencePort.saveNewOrder(order);

        return buildOrderDetailResult(savedOrder, theaterName, productMap);
    }

    @Override
    @Transactional
    public void cancelOrder(Long userId, Long orderId) {

        // 주문 조회
        ProductOrder order = productPersistencePort.findOrderWithItemsById(orderId)
                .orElseThrow(() -> new GeneralException(ProductErrorCode.ORDER_NOT_FOUND));

        // 본인 주문인지 확인
        if (!userId.equals(order.getUserId())) {
            throw new GeneralException(ProductErrorCode.ORDER_NOT_FOUND);
        }

        // 주문 취소
        order.cancel();

        // 재고 복구
        restoreInventories(order);

        productPersistencePort.updateOrderStatus(orderId, order.getStatus());
    }


    private Map<Long, Product> findProductsOrThrow(List<Long> productIds) {

        // 상품 일괄 조회
        Map<Long, Product> productMap = productPersistencePort.findProductsByIds(productIds).stream()
                .collect(Collectors.toMap(Product::getId, p -> p));

        // 누락된 상품 검증
        if (productMap.size() != productIds.size()) {
            throw new GeneralException(ProductErrorCode.PRODUCT_NOT_FOUND);
        }

        return productMap;
    }

    private void decreaseInventories(Long theaterId, List<CreateOrderCommand.OrderItemCommand> items) {

        List<Long> productIds = items.stream()
                .map(CreateOrderCommand.OrderItemCommand::productId)
                .toList();

        // 재고 일괄 조회
        List<Inventory> inventories = productPersistencePort
                .findInventoriesByTheaterIdAndProductIds(theaterId, productIds);

        Map<Long, Inventory> inventoryMap = inventories.stream()
                .collect(Collectors.toMap(Inventory::getProductId, i -> i));

        // 누락된 재고 검증
        if (inventoryMap.size() != productIds.size()) {
            throw new GeneralException(ProductErrorCode.INVENTORY_NOT_FOUND);
        }

        // 재고 차감
        for (CreateOrderCommand.OrderItemCommand item : items) {
            inventoryMap.get(item.productId()).decreaseQuantity(item.quantity());
        }

        // 재고 저장
        productPersistencePort.updateAllInventoryQuantities(inventories);
    }

    private void restoreInventories(ProductOrder order) {
        List<Long> productIds = order.getOrderItems().stream()
                .map(OrderItem::getProductId)
                .toList();

        // 재고 일괄 조회
        List<Inventory> inventories = productPersistencePort
                .findInventoriesByTheaterIdAndProductIds(order.getTheaterId(), productIds);

        Map<Long, Inventory> inventoryMap = inventories.stream()
                .collect(Collectors.toMap(Inventory::getProductId, i -> i));

        // 누락된 재고 검증
        if (inventoryMap.size() != order.getOrderItems().size()) {
            throw new GeneralException(ProductErrorCode.INVENTORY_NOT_FOUND);
        }

        // 재고 복구
        for (OrderItem item : order.getOrderItems()) {
            inventoryMap.get(item.getProductId()).increaseQuantity(item.getQuantity());
        }

        // 재고 저장
        productPersistencePort.updateAllInventoryQuantities(inventories);
    }

    private OrderDetailResult buildOrderDetailResult(ProductOrder savedOrder, String theaterName, Map<Long, Product> productMap) {
        List<OrderDetailResult.OrderItemInfo> itemInfos = savedOrder.getOrderItems().stream()
                .map(item -> new OrderDetailResult.OrderItemInfo(
                        item.getProductId(),
                        productMap.get(item.getProductId()).getName(),
                        item.getQuantity(),
                        item.getPrice(),
                        item.getPrice() * item.getQuantity()
                ))
                .toList();

        return new OrderDetailResult(
                savedOrder.getId(),
                savedOrder.getOrderNumber(),
                savedOrder.getStatus(),
                theaterName,
                itemInfos,
                savedOrder.getTotalPrice(),
                savedOrder.getCreatedAt()
        );
    }
}