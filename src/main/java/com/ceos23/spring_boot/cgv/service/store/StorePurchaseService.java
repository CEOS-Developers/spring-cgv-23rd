package com.ceos23.spring_boot.cgv.service.store;

import com.ceos23.spring_boot.cgv.domain.store.CinemaMenuStock;
import com.ceos23.spring_boot.cgv.domain.store.StorePurchase;
import com.ceos23.spring_boot.cgv.domain.user.User;
import com.ceos23.spring_boot.cgv.dto.store.StorePurchaseRequest;
import com.ceos23.spring_boot.cgv.global.exception.BadRequestException;
import com.ceos23.spring_boot.cgv.global.exception.ErrorCode;
import com.ceos23.spring_boot.cgv.global.exception.NotFoundException;
import com.ceos23.spring_boot.cgv.repository.store.CinemaMenuStockRepository;
import com.ceos23.spring_boot.cgv.repository.store.StorePurchaseRepository;
import com.ceos23.spring_boot.cgv.repository.user.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class StorePurchaseService {

    private final CinemaMenuStockRepository cinemaMenuStockRepository;
    private final StorePurchaseRepository storePurchaseRepository;
    private final UserRepository userRepository;

    public StorePurchaseService(
            CinemaMenuStockRepository cinemaMenuStockRepository,
            StorePurchaseRepository storePurchaseRepository,
            UserRepository userRepository
    ) {
        this.cinemaMenuStockRepository = cinemaMenuStockRepository;
        this.storePurchaseRepository = storePurchaseRepository;
        this.userRepository = userRepository;
    }

    public void purchase(Long userId, StorePurchaseRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.USER_NOT_FOUND));

        CinemaMenuStock stock = cinemaMenuStockRepository
                .findByCinemaIdAndStoreMenuId(request.cinemaId(), request.storeMenuId())
                .orElseThrow(() -> new NotFoundException(ErrorCode.NOT_FOUND));

        if (stock.getStockQuantity() < request.quantity()) {
            throw new BadRequestException(ErrorCode.BAD_REQUEST);
        }

        stock.decreaseStock(request.quantity());

        int totalPrice = stock.getStoreMenu().getPrice() * request.quantity();

        StorePurchase storePurchase = new StorePurchase(
                request.quantity(),
                user,
                stock
        );

        storePurchaseRepository.save(storePurchase);
    }
}