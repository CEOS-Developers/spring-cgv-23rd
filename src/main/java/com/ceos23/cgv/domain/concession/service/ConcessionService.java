package com.ceos23.cgv.domain.concession.service;

import com.ceos23.cgv.domain.cinema.entity.Cinema;
import com.ceos23.cgv.domain.cinema.repository.CinemaRepository;
import com.ceos23.cgv.domain.concession.dto.FoodOrderRequest;
import com.ceos23.cgv.domain.concession.entity.FoodOrder;
import com.ceos23.cgv.domain.concession.entity.Inventory;
import com.ceos23.cgv.domain.concession.entity.OrderItem;
import com.ceos23.cgv.domain.concession.entity.Product;
import com.ceos23.cgv.domain.concession.enums.ProductCategory;
import com.ceos23.cgv.domain.concession.repository.FoodOrderRepository;
import com.ceos23.cgv.domain.concession.repository.InventoryRepository;
import com.ceos23.cgv.domain.concession.repository.OrderItemRepository;
import com.ceos23.cgv.domain.concession.repository.ProductRepository;
import com.ceos23.cgv.domain.user.entity.User;
import com.ceos23.cgv.domain.user.repository.UserRepository;
import com.ceos23.cgv.global.exception.CustomException;
import com.ceos23.cgv.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ConcessionService {

    private final ProductRepository productRepository;
    private final FoodOrderRepository foodOrderRepository;
    private final OrderItemRepository orderItemRepository;
    private final UserRepository userRepository;
    private final CinemaRepository cinemaRepository;
    private final InventoryRepository inventoryRepository;

    /**
     * [GET] 매점의 모든 상품 목록 조회
     */
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    /**
     * [POST] 매점 상품 주문하기 (복합 로직)
     */
    @Transactional
    public FoodOrder createOrder(FoodOrderRequest request) {
        User user = userRepository.findById(request.userId())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        Cinema cinema = cinemaRepository.findById(request.cinemaId())
                .orElseThrow(() -> new CustomException(ErrorCode.CINEMA_NOT_FOUND));

        int calculatedTotalPrice = 0;

        FoodOrder foodOrder = FoodOrder.builder()
                .user(user)
                .cinema(cinema)
                .totalPrice(0)
                .build();
        foodOrderRepository.save(foodOrder);

        // 쿼리 최적화: 주문한 상품의 ID만 추출하여 한 번에 DB에서 조회 (IN 쿼리 발생)
        List<Long> productIds = request.orderItems().stream()
                .map(FoodOrderRequest.OrderItemRequest::productId)
                .toList();

        // 조회한 상품 리스트를 <ID, Product> 형태의 Map으로 변환하여 매핑 성능 향상
        Map<Long, Product> productMap = productRepository.findAllById(productIds).stream()
                .collect(Collectors.toMap(Product::getId, p -> p));

        // saveAll()을 사용하기 위해 빈 리스트 생성
        List<OrderItem> orderItemsToSave = new ArrayList<>();

        for (FoodOrderRequest.OrderItemRequest itemReq : request.orderItems()) {
            // DB 조회 대신 메모리(Map)에서 꺼내어 사용
            Product product = productMap.get(itemReq.productId());
            if (product == null) {
                throw new CustomException(ErrorCode.PRODUCT_NOT_FOUND);
            }

            Inventory inventory = inventoryRepository.findByCinemaIdAndProductId(cinema.getId(), product.getId())
                    .orElseThrow(() -> new CustomException(ErrorCode.INVENTORY_SHORTAGE));

            inventory.removeStock(itemReq.quantity());

            calculatedTotalPrice += (product.getPrice() * itemReq.quantity());

            OrderItem orderItem = OrderItem.builder()
                    .foodOrder(foodOrder)
                    .product(product)
                    .quantity(itemReq.quantity())
                    .build();

            orderItemsToSave.add(orderItem);
        }

        orderItemRepository.saveAll(orderItemsToSave);

        // 더티 체킹으로 총액 자동 업데이트
        foodOrder.updateTotalPrice(calculatedTotalPrice);

        return foodOrder;
    }

    /**
     * [POST] 새로운 매점 상품 등록 (관리자용)
     */
    @Transactional
    public Product createProduct(String name, int price, String description,
                                 String origin, String ingredient,
                                 Boolean pickupPossible, ProductCategory category) {
        Product product = Product.builder()
                .name(name)
                .price(price)
                .description(description)
                .origin(origin)
                .ingredient(ingredient)
                .pickupPossible(pickupPossible)
                .category(category)
                .build();

        return productRepository.save(product);
    }

    /**
     * [GET] 특정 유저의 매점 주문 내역 조회
     */
    public List<FoodOrder> getOrdersByUserId(Long userId) {
        // N+1 문제를 방지하는 페치 조인 메서드 호출
        return foodOrderRepository.findByUserIdWithFetchJoin(userId);
    }}