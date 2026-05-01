package com.ceos.spring_cgv_23rd.domain.product.application.port.out;

import com.ceos.spring_cgv_23rd.domain.product.domain.Inventory;
import com.ceos.spring_cgv_23rd.domain.product.domain.OrderStatus;
import com.ceos.spring_cgv_23rd.domain.product.domain.Product;
import com.ceos.spring_cgv_23rd.domain.product.domain.ProductOrder;

import java.util.List;
import java.util.Optional;

public interface ProductPersistencePort {

    // Product
    List<Product> findProductsByIds(List<Long> productIds);


    // Inventory
    List<Inventory> findInventoriesByTheaterIdAndProductIds(Long theaterId, List<Long> productIds);

    boolean tryDecreaseInventory(Long theaterId, Long productId, int count);

    boolean tryIncreaseInventory(Long theaterId, Long productId, int count);


    // Order
    ProductOrder saveNewOrder(ProductOrder order);

    Optional<ProductOrder> findOrderWithItemsById(Long orderId);

    void updateOrderStatus(Long orderId, OrderStatus status);


    // Theater
    Optional<String> findTheaterNameById(Long theaterId);
}
