package com.ceos23.spring_boot.cgv.service.store;

import com.ceos23.spring_boot.cgv.domain.store.CinemaMenuStock;
import com.ceos23.spring_boot.cgv.domain.store.StorePurchase;
import com.ceos23.spring_boot.cgv.domain.user.User;
import com.ceos23.spring_boot.cgv.dto.store.StorePurchaseRequest;
import com.ceos23.spring_boot.cgv.global.exception.ErrorCode;
import com.ceos23.spring_boot.cgv.global.exception.NotFoundException;
import com.ceos23.spring_boot.cgv.repository.store.CinemaMenuStockRepository;
import com.ceos23.spring_boot.cgv.repository.store.StorePurchaseRepository;
import com.ceos23.spring_boot.cgv.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class StorePurchaseService {

    private final CinemaMenuStockRepository cinemaMenuStockRepository;
    private final StorePurchaseRepository storePurchaseRepository;
    private final UserRepository userRepository;

    public void purchase(Long userId, StorePurchaseRequest request) {
        User user = findUserById(userId);
        CinemaMenuStock stock = findCinemaMenuStock(request);

        stock.decreaseStock(request.quantity());
        storePurchaseRepository.save(new StorePurchase(request.quantity(), user, stock));
    }

    private User findUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.USER_NOT_FOUND));
    }

    private CinemaMenuStock findCinemaMenuStock(StorePurchaseRequest request) {
        return cinemaMenuStockRepository
                .findByCinemaIdAndStoreMenuId(request.cinemaId(), request.storeMenuId())
                .orElseThrow(() -> new NotFoundException(ErrorCode.STORE_MENU_STOCK_NOT_FOUND));
    }
}
