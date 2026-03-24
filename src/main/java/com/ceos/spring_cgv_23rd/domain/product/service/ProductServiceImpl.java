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
import com.ceos.spring_cgv_23rd.domain.product.repository.OrderItemRepository;
import com.ceos.spring_cgv_23rd.domain.product.repository.ProductOrderRepository;
import com.ceos.spring_cgv_23rd.domain.product.repository.ProductRepository;
import com.ceos.spring_cgv_23rd.domain.theater.entity.Theater;
import com.ceos.spring_cgv_23rd.domain.theater.exception.TheaterErrorCode;
import com.ceos.spring_cgv_23rd.domain.theater.repository.TheaterRepository;
import com.ceos.spring_cgv_23rd.domain.user.entity.User;
import com.ceos.spring_cgv_23rd.global.apiPayload.exception.GeneralException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductServiceImpl implements ProductService {

    private final ProductOrderRepository productOrderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductRepository productRepository;
    private final InventoryRepository inventoryRepository;
    private final TheaterRepository theaterRepository;

    @Override
    @Transactional
    public ProductResponseDTO.OrderDetailResponseDTO createOrder(Long userId, ProductRequestDTO.CreateOrderRequestDTO request) {

        // TODO : 주석 제거
        // 유저 조회
//        User user = userRepository.findById(userId)
//                .orElseThrow(() -> new GeneralException(UserErrorCode.USER_NOT_FOUND));
        User user = User.builder()
                .id(userId)
                .build();

        // 영화관 조회
        Theater theater = theaterRepository.findById(request.theaterId())
                .orElseThrow(() -> new GeneralException(TheaterErrorCode.THEATER_NOT_FOUND));


        // 가격 계산 + 재고 차감
        int totalPrice = 0;
        Map<Long, Product> productMap = new HashMap<>();

        for (ProductRequestDTO.OrderItemRequestDTO item : request.items()) {

            Product product = productRepository.findById(item.productId())
                    .orElseThrow(() -> new GeneralException((ProductErrorCode.PRODUCT_NOT_FOUND)));

            Inventory inventory = inventoryRepository.findByTheaterIdAndProductId(request.theaterId(), item.productId())
                    .orElseThrow(() -> new GeneralException(ProductErrorCode.INVENTORY_NOT_FOUND));

            inventory.decreaseQuantity(item.quantity());
            totalPrice += product.getPrice() * item.quantity();
            productMap.put(product.getId(), product);
        }

        // 주문 생성
        ProductOrder order = ProductOrder.createOrder(user, theater, totalPrice);

        productOrderRepository.save(order);


        // 주문 아이템 생성
        List<OrderItem> orderItems = request.items().stream()
                .map(item -> OrderItem.createOrderItem(order, productMap.get(item.productId()), item.quantity()))
                .toList();

        orderItemRepository.saveAll(orderItems);

        return ProductResponseDTO.OrderDetailResponseDTO.of(order, orderItems);
    }

    @Override
    @Transactional
    public void cancelOrder(Long userId, Long orderId) {

        // TODO : 주석 제거
        // 유저 조회
//        User user = userRepository.findById(userId)
//                .orElseThrow(() -> new GeneralException(UserErrorCode.USER_NOT_FOUND));
        User user = User.builder()
                .id(userId)
                .build();

        // 주문 조회
        ProductOrder order = productOrderRepository.findById(orderId)
                .orElseThrow(() -> new GeneralException(ProductErrorCode.ORDER_NOT_FOUND));

        // TODO: 주석 제거
        // 본인 주문인지 확인
//        if (!user.equals(order.getUser())) {
//            throw new GeneralException(ProductErrorCode.ORDER_NOT_FOUND);
//        }

        // 이미 취소된 주문인지 확인
        if (order.getStatus() == OrderStatus.CANCELLED) {
            throw new GeneralException(ProductErrorCode.ALREADY_CANCELLED);
        }

        // 주문 아이템 조회 + 재고 복구
        List<OrderItem> orderItems = orderItemRepository.findByProductOrderId(orderId);
        for (OrderItem item : orderItems) {
            Inventory inventory = inventoryRepository.findByTheaterIdAndProductId(order.getTheater().getId(), item.getProduct().getId())
                    .orElseThrow(() -> new GeneralException(ProductErrorCode.INVENTORY_NOT_FOUND));

            inventory.increaseQuantity(item.getQuantity());
        }

        // 주문 취소
        order.cancel();
    }
}
