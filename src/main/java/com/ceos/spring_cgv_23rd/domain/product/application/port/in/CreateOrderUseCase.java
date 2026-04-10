package com.ceos.spring_cgv_23rd.domain.product.application.port.in;

import com.ceos.spring_cgv_23rd.domain.product.application.dto.command.CreateOrderCommand;
import com.ceos.spring_cgv_23rd.domain.product.application.dto.result.OrderDetailResult;

public interface CreateOrderUseCase {
    OrderDetailResult createOrder(Long userId, CreateOrderCommand command);
}
