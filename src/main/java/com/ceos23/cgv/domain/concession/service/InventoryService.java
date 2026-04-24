package com.ceos23.cgv.domain.concession.service;

import com.ceos23.cgv.domain.cinema.entity.Cinema;
import com.ceos23.cgv.domain.cinema.repository.CinemaRepository;
import com.ceos23.cgv.domain.concession.dto.InventoryUpdateRequest;
import com.ceos23.cgv.domain.concession.entity.Inventory;
import com.ceos23.cgv.domain.concession.entity.Product;
import com.ceos23.cgv.domain.concession.repository.InventoryRepository;
import com.ceos23.cgv.domain.concession.repository.ProductRepository;
import com.ceos23.cgv.global.exception.CustomException;
import com.ceos23.cgv.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class InventoryService {

    private final InventoryRepository inventoryRepository;
    private final CinemaRepository cinemaRepository;
    private final ProductRepository productRepository;

    @Transactional
    public Inventory updateInventory(InventoryUpdateRequest request) {
        Cinema cinema = findCinema(request.cinemaId());
        Product product = findProduct(request.productId());

        return inventoryRepository.findByCinemaIdAndProductId(request.cinemaId(), request.productId())
                .map(inventory -> updateExistingInventory(inventory, request.quantity()))
                .orElseGet(() -> createInventory(cinema, product, request.quantity()));
    }

    private Cinema findCinema(Long cinemaId) {
        return cinemaRepository.findById(cinemaId)
                .orElseThrow(() -> new CustomException(ErrorCode.CINEMA_NOT_FOUND));
    }

    private Product findProduct(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new CustomException(ErrorCode.PRODUCT_NOT_FOUND));
    }

    private Inventory updateExistingInventory(Inventory inventory, int quantity) {
        inventory.changeStockBy(quantity);
        return inventory;
    }

    private Inventory createInventory(Cinema cinema, Product product, int quantity) {
        return inventoryRepository.save(Inventory.create(cinema, product, quantity));
    }

    public List<Inventory> getInventoriesByCinemaId(Long cinemaId) {
        return inventoryRepository.findByCinemaId(cinemaId);
    }
}
