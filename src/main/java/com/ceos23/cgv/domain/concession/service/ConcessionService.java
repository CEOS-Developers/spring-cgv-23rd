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
        User user = findUser(request.userId());
        Cinema cinema = findCinema(request.cinemaId());
        FoodOrder foodOrder = FoodOrder.create(user, cinema);
        foodOrderRepository.save(foodOrder);

        Map<Long, Product> productMap = loadProductMap(request);
        List<OrderItem> orderItems = createOrderItems(request, cinema, foodOrder, productMap);
        orderItemRepository.saveAll(orderItems);

        foodOrder.updateTotalPrice(calculateTotalPrice(orderItems));

        return foodOrder;
    }

    private User findUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
    }

    private Cinema findCinema(Long cinemaId) {
        return cinemaRepository.findById(cinemaId)
                .orElseThrow(() -> new CustomException(ErrorCode.CINEMA_NOT_FOUND));
    }

    private Map<Long, Product> loadProductMap(FoodOrderRequest request) {
        List<Long> productIds = request.orderItems().stream()
                .map(FoodOrderRequest.OrderItemRequest::productId)
                .toList();

        return productRepository.findAllById(productIds).stream()
                .collect(Collectors.toMap(Product::getId, product -> product));
    }

    private List<OrderItem> createOrderItems(FoodOrderRequest request, Cinema cinema,
                                             FoodOrder foodOrder, Map<Long, Product> productMap) {
        return request.orderItems().stream()
                .map(itemReq -> createOrderItem(foodOrder, cinema.getId(), productMap, itemReq))
                .toList();
    }

    private OrderItem createOrderItem(FoodOrder foodOrder, Long cinemaId,
                                      Map<Long, Product> productMap,
                                      FoodOrderRequest.OrderItemRequest itemReq) {
        Product product = getRequiredProduct(productMap, itemReq.productId());
        decreaseInventoryStock(cinemaId, product.getId(), itemReq.quantity());

        return OrderItem.create(foodOrder, product, itemReq.quantity());
    }

    private Product getRequiredProduct(Map<Long, Product> productMap, Long productId) {
        Product product = productMap.get(productId);
        if (product == null) {
            throw new CustomException(ErrorCode.PRODUCT_NOT_FOUND);
        }

        return product;
    }

    private void decreaseInventoryStock(Long cinemaId, Long productId, int quantity) {
        Inventory inventory = inventoryRepository.findByCinemaIdAndProductId(cinemaId, productId)
                .orElseThrow(() -> new CustomException(ErrorCode.INVENTORY_SHORTAGE));

        inventory.removeStock(quantity);
    }

    private int calculateTotalPrice(List<OrderItem> orderItems) {
        return orderItems.stream()
                .mapToInt(orderItem -> orderItem.getProduct().getPrice() * orderItem.getQuantity())
                .sum();
    }

    /**
     * [POST] 새로운 매점 상품 등록 (관리자용)
     */
    @Transactional
    public Product createProduct(String name, int price, String description,
                                 String origin, String ingredient,
                                 Boolean pickupPossible, ProductCategory category) {
        Product product = Product.create(name, price, description, origin, ingredient, pickupPossible, category);
        return productRepository.save(product);
    }

    /**
     * [GET] 특정 유저의 매점 주문 내역 조회
     */
    public List<FoodOrder> getOrdersByUserId(Long userId) {
        // N+1 문제를 방지하는 페치 조인 메서드 호출
        return foodOrderRepository.findByUserIdWithFetchJoin(userId);
    }
}
