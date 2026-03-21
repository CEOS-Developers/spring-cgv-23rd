package com.ceos.spring_cgv_23rd.domain.product.service;

import com.ceos.spring_cgv_23rd.domain.product.dto.ProductRequestDTO;
import com.ceos.spring_cgv_23rd.domain.product.dto.ProductResponseDTO;

public interface ProductService {

    ProductResponseDTO.OrderDetailResponseDTO createOrder(Long userId, ProductRequestDTO.CreateOrderRequestDTO request);

    void cancelOrder(Long userId, Long orderId);
}
