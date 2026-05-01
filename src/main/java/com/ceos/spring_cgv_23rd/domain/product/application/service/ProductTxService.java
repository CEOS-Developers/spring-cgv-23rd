package com.ceos.spring_cgv_23rd.domain.product.application.service;

import com.ceos.spring_cgv_23rd.domain.product.application.dto.command.CreateOrderCommand;
import com.ceos.spring_cgv_23rd.domain.product.application.port.out.ProductPersistencePort;
import com.ceos.spring_cgv_23rd.domain.product.domain.OrderItem;
import com.ceos.spring_cgv_23rd.domain.product.domain.Product;
import com.ceos.spring_cgv_23rd.domain.product.domain.ProductOrder;
import com.ceos.spring_cgv_23rd.domain.product.exception.ProductErrorCode;
import com.ceos.spring_cgv_23rd.global.apiPayload.exception.GeneralException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductTxService {

    private final ProductPersistencePort productPersistencePort;

    @Transactional
    public ProductOrder decreaseAndSaveOrder(Long userId, Long theaterId, String paymentId,
                                             List<CreateOrderCommand.OrderItemCommand> items,
                                             Map<Long, Product> productMap) {

        // productId를 기준으로 정렬
        List<CreateOrderCommand.OrderItemCommand> sortedItems = items.stream()
                .sorted(Comparator.comparing(CreateOrderCommand.OrderItemCommand::productId))
                .toList();

        // 재고 차감
        for (CreateOrderCommand.OrderItemCommand item : sortedItems) {
            boolean ok = productPersistencePort.tryDecreaseInventory(theaterId, item.productId(), item.quantity());

            if (!ok) {
                throw new GeneralException(ProductErrorCode.INSUFFICIENT_STOCK);
            }
        }

        // 주문 생성
        List<OrderItem> orderItems = items.stream()
                .map(i -> {
                    Product product = productMap.get(i.productId());
                    return OrderItem.createOrderItem(product.getId(), product.getPrice(), i.quantity());
                })
                .toList();

        ProductOrder order = ProductOrder.createOrder(userId, theaterId, paymentId, orderItems);

        // 주문 저장
        return productPersistencePort.saveNewOrder(order);
    }

    @Transactional
    public void applyCancellation(ProductOrder order) {

        // 주문 상태 취소 처리
        order.cancel();

        // productId를 기준으로 정렬
        List<OrderItem> orderItems = order.getOrderItems().stream()
                .sorted(Comparator.comparing(OrderItem::getProductId))
                .toList();

        // 재고 복구
        for (OrderItem item : orderItems) {
            boolean ok = productPersistencePort.tryIncreaseInventory(order.getTheaterId(), item.getProductId(), item.getQuantity());

            if (!ok) {
                throw new GeneralException(ProductErrorCode.INVENTORY_NOT_FOUND);
            }
        }

        // 주문 상태 DB 반영
        productPersistencePort.updateOrderStatus(order.getId(), order.getStatus());
    }
}
