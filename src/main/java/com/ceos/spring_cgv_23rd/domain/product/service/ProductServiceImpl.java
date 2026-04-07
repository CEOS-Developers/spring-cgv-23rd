package com.ceos.spring_cgv_23rd.domain.product.service;

import com.ceos.spring_cgv_23rd.domain.product.dto.ProductRequestDTO;
import com.ceos.spring_cgv_23rd.domain.product.dto.ProductResponseDTO;
import com.ceos.spring_cgv_23rd.domain.product.entity.Inventory;
import com.ceos.spring_cgv_23rd.domain.product.entity.OrderItem;
import com.ceos.spring_cgv_23rd.domain.product.entity.Product;
import com.ceos.spring_cgv_23rd.domain.product.entity.ProductOrder;
import com.ceos.spring_cgv_23rd.domain.product.enums.OrderStatus;
import com.ceos.spring_cgv_23rd.domain.product.exception.ProductErrorCode;
import com.ceos.spring_cgv_23rd.domain.product.repository.InventoryRepository;
import com.ceos.spring_cgv_23rd.domain.product.repository.ProductOrderRepository;
import com.ceos.spring_cgv_23rd.domain.product.repository.ProductRepository;
import com.ceos.spring_cgv_23rd.domain.theater.adapter.out.persistence.entity.TheaterEntity;
import com.ceos.spring_cgv_23rd.domain.theater.adapter.out.persistence.repository.TheaterJpaRepository;
import com.ceos.spring_cgv_23rd.domain.theater.exception.TheaterErrorCode;
import com.ceos.spring_cgv_23rd.domain.user.entity.User;
import com.ceos.spring_cgv_23rd.domain.user.exception.UserErrorCode;
import com.ceos.spring_cgv_23rd.domain.user.repository.UserRepository;
import com.ceos.spring_cgv_23rd.global.apiPayload.exception.GeneralException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductServiceImpl implements ProductService {

    private final ProductOrderRepository productOrderRepository;
    private final ProductRepository productRepository;
    private final InventoryRepository inventoryRepository;
    private final TheaterJpaRepository theaterJpaRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public ProductResponseDTO.OrderDetailResponseDTO createOrder(Long userId, ProductRequestDTO.CreateOrderRequestDTO request) {

        // 유저 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new GeneralException(UserErrorCode.USER_NOT_FOUND));

        // 영화관 조회
        TheaterEntity theater = theaterJpaRepository.findById(request.theaterId())
                .orElseThrow(() -> new GeneralException(TheaterErrorCode.THEATER_NOT_FOUND));

        // 요청한 상품 ID 목록 추출
        List<Long> productIds = request.items().stream()
                .map(ProductRequestDTO.OrderItemRequestDTO::productId)
                .toList();

        // 상품 일괄 조회
        Map<Long, Product> productMap = productRepository.findAllByIdIn(productIds).stream()
                .collect(Collectors.toMap(Product::getId, p -> p));

        if (productMap.size() != productIds.size()) {
            throw new GeneralException(ProductErrorCode.PRODUCT_NOT_FOUND);
        }

        // 재고 일괄 조회
        Map<Long, Inventory> inventoryMap = inventoryRepository.findAllByTheaterIdAndProductIdIn(request.theaterId(), productIds).stream()
                .collect(Collectors.toMap(i -> i.getProduct().getId(), i -> i));

        if (inventoryMap.size() != productIds.size()) {
            throw new GeneralException(ProductErrorCode.INVENTORY_NOT_FOUND);
        }

        // 재고 차감
        for (ProductRequestDTO.OrderItemRequestDTO item : request.items()) {
            Inventory inventory = inventoryMap.get(item.productId());
            inventory.decreaseQuantity(item.quantity());
        }

        // 주문 아이템 생성
        List<OrderItem> orderItems = request.items().stream()
                .map(item -> OrderItem.createOrderItem(productMap.get(item.productId()), item.quantity()))
                .toList();

        // 주문 생성
        ProductOrder order = ProductOrder.createOrder(user, theater, orderItems);
        productOrderRepository.save(order);

        return ProductResponseDTO.OrderDetailResponseDTO.of(order);
    }

    @Override
    @Transactional
    public void cancelOrder(Long userId, Long orderId) {

        // 유저 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new GeneralException(UserErrorCode.USER_NOT_FOUND));

        // 주문 조회
        ProductOrder order = productOrderRepository.findWithOrderItemsById(orderId)
                .orElseThrow(() -> new GeneralException(ProductErrorCode.ORDER_NOT_FOUND));

        // 본인 주문인지 확인
        if (!user.equals(order.getUser())) {
            throw new GeneralException(ProductErrorCode.ORDER_NOT_FOUND);
        }

        // 이미 취소된 주문인지 확인
        if (order.getStatus() == OrderStatus.CANCELLED) {
            throw new GeneralException(ProductErrorCode.ALREADY_CANCELLED);
        }

        // 상품 조회
        List<OrderItem> orderItems = order.getOrderItems();
        List<Long> productIds = orderItems.stream()
                .map(item -> item.getProduct().getId())
                .toList();

        // 영화관 재고 조회
        Map<Long, Inventory> inventoryMap = inventoryRepository
                .findAllByTheaterIdAndProductIdIn(order.getTheater().getId(), productIds).stream()
                .collect(Collectors.toMap(i -> i.getProduct().getId(), i -> i));

        if (inventoryMap.size() != orderItems.size()) {
            throw new GeneralException(ProductErrorCode.INVENTORY_NOT_FOUND);
        }

        // 재고 복구
        for (OrderItem item : orderItems) {
            Inventory inventory = inventoryMap.get(item.getProduct().getId());

            inventory.increaseQuantity(item.getQuantity());
        }

        // 주문 취소
        order.cancel();
    }
}
