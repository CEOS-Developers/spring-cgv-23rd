package com.ceos.spring_cgv_23rd.domain.product.adapter.in.web.mapper;

import com.ceos.spring_cgv_23rd.domain.product.adapter.in.web.dto.request.ProductRequest;
import com.ceos.spring_cgv_23rd.domain.product.application.dto.command.CreateOrderCommand;
import org.springframework.stereotype.Component;

@Component
public class ProductRequestMapper {

    public CreateOrderCommand toCommand(ProductRequest.CreateOrderRequest request) {
        return new CreateOrderCommand(
                request.theaterId(),
                request.items().stream()
                        .map(item -> new CreateOrderCommand.OrderItemCommand(
                                item.productId(),
                                item.quantity()
                        ))
                        .toList()
        );
    }
}
