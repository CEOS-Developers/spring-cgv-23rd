package com.ceos23.spring_boot.service;

import com.ceos23.spring_boot.domain.ItemOrder;
import com.ceos23.spring_boot.domain.OrderStatus;
import com.ceos23.spring_boot.dto.ItemOrderRequest;
import com.ceos23.spring_boot.dto.ItemOrderResponse;
import com.ceos23.spring_boot.dto.OrderItemRequest;
import com.ceos23.spring_boot.exception.CustomException;
import com.ceos23.spring_boot.global.exception.ErrorCode;
import com.ceos23.spring_boot.infra.payment.PaymentGateway;
import com.ceos23.spring_boot.infra.payment.dto.PaymentData;
import com.ceos23.spring_boot.repository.ItemOrderRepository;
import com.ceos23.spring_boot.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemOrderService {

    private static final DateTimeFormatter PAYMENT_ID_FORMATTER =
            DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    private final ItemOrderRepository itemOrderRepository;
    private final UserRepository userRepository;
    private final PaymentGateway paymentGateway;
    private final ItemOrderTransactionService itemOrderTransactionService;

    public ItemOrderResponse orderItems(ItemOrderRequest request) {
        validateRequest(request);

        ItemOrder savedOrder = itemOrderTransactionService.createOrderAndDecreaseStock(request);
        String paymentId = createPaymentId(savedOrder.getId());

        try {
            PaymentData payment = paymentGateway.pay(
                    paymentId,
                    createOrderName(savedOrder),
                    savedOrder.getTotalPrice(),
                    createCustomData(savedOrder)
            );

            ItemOrder paidOrder = itemOrderTransactionService.markPaid(
                    savedOrder.getId(),
                    paymentId,
                    resolvePaidAt(payment)
            );

            return ItemOrderResponse.from(paidOrder);

        } catch (CustomException e) {
            itemOrderTransactionService.markPaymentFailedAndRestoreStock(savedOrder.getId(), paymentId);
            handlePaymentException(e);
            throw e;
        }
    }

    private void handlePaymentException(CustomException e) {
        if (e.getErrorCode() == ErrorCode.PAYMENT_SERVER_ERROR) {
            throw new CustomException(ErrorCode.PAYMENT_RETRY_FAILED);
        }

        throw e;
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

    public ItemOrderResponse cancelOrder(Long orderId) {
        validateId(orderId);

        ItemOrder itemOrder = itemOrderRepository.findById(orderId)
                .orElseThrow(() -> new CustomException(ErrorCode.ITEM_ORDER_NOT_FOUND));

        if (itemOrder.getOrderStatus() != OrderStatus.PAID) {
            throw new CustomException(ErrorCode.ITEM_ORDER_NOT_PAID);
        }

        paymentGateway.cancel(itemOrder.getPaymentId());

        ItemOrder cancelledOrder = itemOrderTransactionService.cancelOrder(orderId);

        return ItemOrderResponse.from(cancelledOrder);
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
        if (payment.paidAt() != null) {
            return payment.paidAt();
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
            throw new CustomException(ErrorCode.INVALID_ORDER_REQUEST);
        }

        validateId(request.getUserId());
        validateId(request.getTheaterId());

        if (request.getItems() == null || request.getItems().isEmpty()) {
            throw new CustomException(ErrorCode.INVALID_ORDER_REQUEST);
        }

        for (OrderItemRequest orderItemRequest : request.getItems()) {
            validateOrderItem(orderItemRequest);
        }
    }

    private void validateOrderItem(OrderItemRequest orderItemRequest) {
        if (orderItemRequest == null) {
            throw new CustomException(ErrorCode.INVALID_ORDER_ITEM);
        }

        validateId(orderItemRequest.getItemId());

        if (orderItemRequest.getCount() == null || orderItemRequest.getCount() <= 0) {
            throw new CustomException(ErrorCode.INVALID_ORDER_ITEM);
        }
    }

    private void validateId(Long id) {
        if (id == null || id <= 0) {
            throw new CustomException(ErrorCode.INVALID_ID_VALUE);
        }
    }
}