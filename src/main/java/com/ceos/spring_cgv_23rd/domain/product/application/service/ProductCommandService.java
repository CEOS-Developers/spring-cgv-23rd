package com.ceos.spring_cgv_23rd.domain.product.application.service;

import com.ceos.spring_cgv_23rd.domain.payment.application.dto.command.PayCommand;
import com.ceos.spring_cgv_23rd.domain.payment.application.dto.result.PaymentResult;
import com.ceos.spring_cgv_23rd.domain.payment.application.port.in.CancelPaymentUseCase;
import com.ceos.spring_cgv_23rd.domain.payment.application.port.in.PaymentUseCase;
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

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProductCommandService implements CreateOrderUseCase, CancelOrderUseCase {

    private final ProductPersistencePort productPersistencePort;
    private final ProductTxService productTxService;
    private final PaymentUseCase paymentUseCase;
    private final CancelPaymentUseCase cancelPaymentUseCase;

    @Override
    public OrderDetailResult createOrder(Long userId, CreateOrderCommand command) {
        if (command.items() == null || command.items().isEmpty()) {
            throw new GeneralException(ProductErrorCode.EMPTY_ORDER_ITEMS);
        }


        // 영화관명 조회
        String theaterName = productPersistencePort.findTheaterNameById(command.theaterId())
                .orElseThrow(() -> new GeneralException(TheaterErrorCode.THEATER_NOT_FOUND));

        // 요청한 상품 ID 목록 추출
        List<Long> productIds = command.items().stream()
                .map(CreateOrderCommand.OrderItemCommand::productId)
                .toList();

        // 중복된 상품이 있는지 검사
        if (productIds.size() != new HashSet<>(productIds).size()) {
            throw new GeneralException(ProductErrorCode.DUPLICATE_ORDER_ITEM);
        }

        // 상품 일괄 조회
        Map<Long, Product> productMap = findProductsOrThrow(productIds);

        // 3. 인벤토리 검증
        verifyInventoryRowsExist(command.theaterId(), productIds);

        // 총액 계산
        int amount = command.items().stream()
                .mapToInt(i -> productMap.get(i.productId()).getPrice() * i.quantity())
                .sum();

        // 주문명 생성
        Product firstProduct = productMap.get(command.items().getFirst().productId());
        String orderName = ProductOrder.generateOrderName(firstProduct.getName(), command.items().size());

        PaymentResult payment = paymentUseCase.pay(new PayCommand(command.paymentId(), orderName, amount));

        // PG 결제
        ProductOrder savedOrder;
        try {
            savedOrder = productTxService.decreaseAndSaveOrder(userId, command.theaterId(), command.paymentId(), command.items(), productMap);
        } catch (Exception e) {
            log.warn("매점 주문 실패. 결제 취소 시도. paymentId={}", command.paymentId(), e);

            try {
                cancelPaymentUseCase.cancel(command.paymentId());
            } catch (Exception ex) {
                log.error("보상 결제 취소 실패. paymentId={}", command.paymentId(), ex);
            }

            throw new GeneralException(ProductErrorCode.CONFIRM_FAILED_ROLLED_BACK);
        }

        return buildOrderDetailResult(savedOrder, theaterName, productMap, payment);
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

    private void verifyInventoryRowsExist(Long theaterId, List<Long> productIds) {
        List<Inventory> inventories = productPersistencePort.findInventoriesByTheaterIdAndProductIds(theaterId, productIds);

        if (inventories.size() != productIds.size()) {
            throw new GeneralException(ProductErrorCode.INVENTORY_NOT_FOUND);
        }
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

    private OrderDetailResult buildOrderDetailResult(ProductOrder savedOrder, String theaterName,
                                                     Map<Long, Product> productMap, PaymentResult payment) {
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
                savedOrder.getCreatedAt(),
                payment
        );
    }
}