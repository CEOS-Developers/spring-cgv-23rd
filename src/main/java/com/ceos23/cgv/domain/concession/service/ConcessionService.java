package com.ceos23.cgv.domain.concession.service;

import com.ceos23.cgv.domain.cinema.entity.Cinema;
import com.ceos23.cgv.domain.cinema.repository.CinemaRepository;
import com.ceos23.cgv.domain.concession.dto.FoodOrderRequest;
import com.ceos23.cgv.domain.concession.entity.FoodOrder;
import com.ceos23.cgv.domain.concession.entity.OrderItem;
import com.ceos23.cgv.domain.concession.entity.Product;
import com.ceos23.cgv.domain.concession.enums.ProductCategory;
import com.ceos23.cgv.domain.concession.repository.FoodOrderRepository;
import com.ceos23.cgv.domain.concession.repository.OrderItemRepository;
import com.ceos23.cgv.domain.concession.repository.ProductRepository;
import com.ceos23.cgv.domain.user.entity.User;
import com.ceos23.cgv.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ConcessionService {

    private final ProductRepository productRepository;
    private final FoodOrderRepository foodOrderRepository;
    private final OrderItemRepository orderItemRepository;
    private final UserRepository userRepository;
    private final CinemaRepository cinemaRepository;

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
        // 1. 유저와 픽업할 영화관 지점 조회
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다."));
        Cinema cinema = cinemaRepository.findById(request.getCinemaId())
                .orElseThrow(() -> new IllegalArgumentException("영화관 지점을 찾을 수 없습니다."));

        // 2. 총 결제 금액 계산을 위한 변수
        int calculatedTotalPrice = 0;

        // 3. 주문(FoodOrder) 엔티티 먼저 생성 (아직 총액은 0원으로 임시 세팅)
        FoodOrder foodOrder = FoodOrder.builder()
                .user(user)
                .cinema(cinema)
                .totalPrice(0)
                .build();
        foodOrderRepository.save(foodOrder); // OrderItem과 연결하기 위해 먼저 저장(ID 발급)

        // 4. 장바구니에 담긴 아이템(OrderItem)들을 하나씩 꺼내서 처리
        for (FoodOrderRequest.OrderItemRequest itemReq : request.getOrderItems()) {
            Product product = productRepository.findById(itemReq.getProductId())
                    .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다."));

            // 개별 아이템 가격 누적
            calculatedTotalPrice += (product.getPrice() * itemReq.getQuantity());

            // OrderItem 엔티티 생성 및 저장
            OrderItem orderItem = OrderItem.builder()
                    .foodOrder(foodOrder)
                    .product(product)
                    .quantity(itemReq.getQuantity())
                    .build();
            orderItemRepository.save(orderItem);
        }

        // 5. 최종 계산된 총액을 FoodOrder에 업데이트
        FoodOrder finalOrder = FoodOrder.builder()
                .id(foodOrder.getId())
                .user(user)
                .cinema(cinema)
                .totalPrice(calculatedTotalPrice)
                .build();

        return foodOrderRepository.save(finalOrder);
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
        return foodOrderRepository.findByUserId(userId);
    }
}