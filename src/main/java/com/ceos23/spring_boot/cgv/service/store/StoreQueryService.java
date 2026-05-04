package com.ceos23.spring_boot.cgv.service.store;

import com.ceos23.spring_boot.cgv.domain.store.CinemaMenuStock;
import com.ceos23.spring_boot.cgv.domain.store.StorePurchase;
import com.ceos23.spring_boot.cgv.global.exception.ErrorCode;
import com.ceos23.spring_boot.cgv.global.exception.NotFoundException;
import com.ceos23.spring_boot.cgv.repository.cinema.CinemaRepository;
import com.ceos23.spring_boot.cgv.repository.store.CinemaMenuStockRepository;
import com.ceos23.spring_boot.cgv.repository.store.StorePurchaseRepository;
import com.ceos23.spring_boot.cgv.repository.user.UserRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StoreQueryService {

    private final CinemaRepository cinemaRepository;
    private final CinemaMenuStockRepository cinemaMenuStockRepository;
    private final StorePurchaseRepository storePurchaseRepository;
    private final UserRepository userRepository;

    public List<CinemaMenuStock> getStoreMenus(Long cinemaId) {
        ensureCinemaExists(cinemaId);
        return cinemaMenuStockRepository.findAllByCinemaIdOrderByStoreMenuNameAsc(cinemaId);
    }

    public List<StorePurchase> getPurchaseHistory(Long userId) {
        ensureUserExists(userId);
        return storePurchaseRepository.findAllByUserIdOrderByPurchasedAtDesc(userId);
    }

    private void ensureCinemaExists(Long cinemaId) {
        if (!cinemaRepository.existsById(cinemaId)) {
            throw new NotFoundException(ErrorCode.CINEMA_NOT_FOUND);
        }
    }

    private void ensureUserExists(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException(ErrorCode.USER_NOT_FOUND);
        }
    }
}
