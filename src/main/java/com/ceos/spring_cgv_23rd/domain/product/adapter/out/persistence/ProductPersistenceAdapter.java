package com.ceos.spring_cgv_23rd.domain.product.adapter.out.persistence;

import com.ceos.spring_cgv_23rd.domain.product.adapter.out.persistence.entity.InventoryEntity;
import com.ceos.spring_cgv_23rd.domain.product.adapter.out.persistence.entity.ProductEntity;
import com.ceos.spring_cgv_23rd.domain.product.adapter.out.persistence.entity.ProductOrderEntity;
import com.ceos.spring_cgv_23rd.domain.product.adapter.out.persistence.mapper.ProductPersistenceMapper;
import com.ceos.spring_cgv_23rd.domain.product.adapter.out.persistence.repository.InventoryJpaRepository;
import com.ceos.spring_cgv_23rd.domain.product.adapter.out.persistence.repository.ProductJpaRepository;
import com.ceos.spring_cgv_23rd.domain.product.adapter.out.persistence.repository.ProductOrderJpaRepository;
import com.ceos.spring_cgv_23rd.domain.product.application.port.out.ProductPersistencePort;
import com.ceos.spring_cgv_23rd.domain.product.domain.*;
import com.ceos.spring_cgv_23rd.domain.product.exception.ProductErrorCode;
import com.ceos.spring_cgv_23rd.domain.theater.adapter.out.persistence.entity.TheaterEntity;
import com.ceos.spring_cgv_23rd.domain.theater.adapter.out.persistence.repository.TheaterJpaRepository;
import com.ceos.spring_cgv_23rd.global.apiPayload.exception.GeneralException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ProductPersistenceAdapter implements ProductPersistencePort {

    private final ProductJpaRepository productJpaRepository;
    private final ProductOrderJpaRepository productOrderJpaRepository;
    private final InventoryJpaRepository inventoryJpaRepository;
    private final TheaterJpaRepository theaterJpaRepository;
    private final ProductPersistenceMapper mapper;

    @Override
    public List<Product> findProductsByIds(List<Long> productIds) {
        return productJpaRepository.findAllByIdIn(productIds).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<Inventory> findInventoriesByTheaterIdAndProductIds(Long theaterId, List<Long> productIds) {
        return inventoryJpaRepository.findAllByTheaterIdAndProductIdIn(theaterId, productIds).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public void updateAllInventoryQuantities(List<Inventory> inventories) {
        for (Inventory inv : inventories) {
            InventoryEntity entity = inventoryJpaRepository.findById(inv.getId())
                    .orElseThrow(() -> new GeneralException(ProductErrorCode.INVENTORY_NOT_FOUND));

            entity.updateQuantity(inv.getQuantity());
        }
    }

    @Override
    public boolean tryDecreaseInventory(Long theaterId, Long productId, int count) {
        return inventoryJpaRepository.decreaseQuantityIfEnough(theaterId, productId, count) > 0;
    }

    @Override
    public ProductOrder saveNewOrder(ProductOrder order) {
        List<Long> productIds = order.getOrderItems().stream()
                .map(OrderItem::getProductId)
                .toList();

        Map<Long, ProductEntity> productEntityMap = productJpaRepository.findAllByIdIn(productIds).stream()
                .collect(Collectors.toMap(ProductEntity::getId, productEntity -> productEntity));

        ProductOrderEntity entity = mapper.toEntity(order, productEntityMap);
        ProductOrderEntity savedEntity = productOrderJpaRepository.save(entity);

        return mapper.toDomain(savedEntity);
    }

    @Override
    public Optional<ProductOrder> findOrderWithItemsById(Long orderId) {
        return productOrderJpaRepository.findWithOrderItemsById(orderId)
                .map(mapper::toDomain);
    }

    @Override
    public void updateOrderStatus(Long orderId, OrderStatus status) {
        ProductOrderEntity entity = productOrderJpaRepository.findById(orderId)
                .orElseThrow(() -> new GeneralException(ProductErrorCode.ORDER_NOT_FOUND));

        entity.updateStatus(status);
    }

    @Override
    public Optional<String> findTheaterNameById(Long theaterId) {
        return theaterJpaRepository.findById(theaterId)
                .map(TheaterEntity::getName);
    }
}
