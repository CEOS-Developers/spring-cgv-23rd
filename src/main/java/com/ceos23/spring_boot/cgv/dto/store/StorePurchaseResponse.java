package com.ceos23.spring_boot.cgv.dto.store;

import com.ceos23.spring_boot.cgv.domain.store.StorePurchase;
import java.time.LocalDateTime;

public record StorePurchaseResponse(
        Long purchaseId,
        Long cinemaId,
        String cinemaName,
        Long storeMenuId,
        String menuName,
        Integer quantity,
        Integer totalPrice,
        LocalDateTime purchasedAt
) {
    public static StorePurchaseResponse from(StorePurchase storePurchase) {
        return new StorePurchaseResponse(
                storePurchase.getId(),
                storePurchase.getCinemaMenuStock().getCinema().getId(),
                storePurchase.getCinemaMenuStock().getCinema().getName(),
                storePurchase.getCinemaMenuStock().getStoreMenu().getId(),
                storePurchase.getCinemaMenuStock().getStoreMenu().getName(),
                storePurchase.getQuantity(),
                storePurchase.getTotalPrice(),
                storePurchase.getPurchasedAt()
        );
    }
}
