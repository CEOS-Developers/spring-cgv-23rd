package com.ceos.spring_cgv_23rd.domain.product.adapter.out.persistence.mapper;

import com.ceos.spring_cgv_23rd.domain.product.adapter.out.persistence.entity.InventoryEntity;
import com.ceos.spring_cgv_23rd.domain.product.adapter.out.persistence.entity.OrderItemEntity;
import com.ceos.spring_cgv_23rd.domain.product.adapter.out.persistence.entity.ProductEntity;
import com.ceos.spring_cgv_23rd.domain.product.adapter.out.persistence.entity.ProductOrderEntity;
import com.ceos.spring_cgv_23rd.domain.product.domain.Inventory;
import com.ceos.spring_cgv_23rd.domain.product.domain.OrderItem;
import com.ceos.spring_cgv_23rd.domain.product.domain.Product;
import com.ceos.spring_cgv_23rd.domain.product.domain.ProductOrder;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.stream.Collectors;

@Component
public class ProductPersistenceMapper {

    //  Entity → Domain

    public Product toDomain(ProductEntity entity) {
        return Product.builder()
                .id(entity.getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .category(entity.getCategory())
                .price(entity.getPrice())
                .productImageUrl(entity.getProductImageUrl())
                .build();
    }

    public ProductOrder toDomain(ProductOrderEntity entity) {
        return ProductOrder.builder()
                .id(entity.getId())
                .userId(entity.getUserId())
                .theaterId(entity.getTheaterId())
                .orderNumber(entity.getOrderNumber())
                .paymentId(entity.getPaymentId())
                .totalPrice(entity.getTotalPrice())
                .status(entity.getStatus())
                .orderItems(entity.getOrderItems().stream()
                        .map(this::toDomain)
                        .collect(Collectors.toList()))
                .createdAt(entity.getCreatedAt())
                .build();
    }

    public Inventory toDomain(InventoryEntity entity) {
        return Inventory.builder()
                .id(entity.getId())
                .theaterId(entity.getTheaterId())
                .productId(entity.getProduct().getId())
                .quantity(entity.getQuantity())
                .build();
    }

    public OrderItem toDomain(OrderItemEntity entity) {
        return OrderItem.builder()
                .id(entity.getId())
                .productId(entity.getProduct().getId())
                .quantity(entity.getQuantity())
                .price(entity.getPrice())
                .build();
    }


    // Domain → Entity

    public ProductOrderEntity toEntity(ProductOrder domain, Map<Long, ProductEntity> productEntityMap) {
        ProductOrderEntity entity = ProductOrderEntity.builder()
                .userId(domain.getUserId())
                .theaterId(domain.getTheaterId())
                .orderNumber(domain.getOrderNumber())
                .paymentId(domain.getPaymentId())
                .totalPrice(domain.getTotalPrice())
                .status(domain.getStatus())
                .build();

        domain.getOrderItems().forEach(item -> {
            ProductEntity productEntity = productEntityMap.get(item.getProductId());
            OrderItemEntity itemEntity = OrderItemEntity.builder()
                    .product(productEntity)
                    .quantity(item.getQuantity())
                    .price(item.getPrice())
                    .build();
            entity.addOrderItem(itemEntity);
        });

        return entity;
    }
}
