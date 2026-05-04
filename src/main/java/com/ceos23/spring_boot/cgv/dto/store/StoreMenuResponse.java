package com.ceos23.spring_boot.cgv.dto.store;

import com.ceos23.spring_boot.cgv.domain.store.CinemaMenuStock;

public record StoreMenuResponse(
        Long cinemaId,
        String cinemaName,
        Long storeMenuId,
        String menuName,
        Integer price,
        Integer stockQuantity
) {
    public static StoreMenuResponse from(CinemaMenuStock cinemaMenuStock) {
        return new StoreMenuResponse(
                cinemaMenuStock.getCinema().getId(),
                cinemaMenuStock.getCinema().getName(),
                cinemaMenuStock.getStoreMenu().getId(),
                cinemaMenuStock.getStoreMenu().getName(),
                cinemaMenuStock.getStoreMenu().getPrice(),
                cinemaMenuStock.getStockQuantity()
        );
    }
}
