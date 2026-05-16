package com.ceos23.spring_boot.cgv.service.store;

import com.ceos23.spring_boot.cgv.domain.store.CinemaMenuStock;
import com.ceos23.spring_boot.cgv.domain.store.StorePurchase;
import com.ceos23.spring_boot.cgv.domain.user.User;
import com.ceos23.spring_boot.cgv.dto.store.StorePurchaseRequest;
import com.ceos23.spring_boot.cgv.global.cache.CacheNames;
import com.ceos23.spring_boot.cgv.global.exception.BadRequestException;
import com.ceos23.spring_boot.cgv.global.exception.ErrorCode;
import com.ceos23.spring_boot.cgv.global.exception.NotFoundException;
import com.ceos23.spring_boot.cgv.global.logging.AuditLogService;
import com.ceos23.spring_boot.cgv.global.logging.BusinessMetricRecorder;
import com.ceos23.spring_boot.cgv.repository.store.CinemaMenuStockRepository;
import com.ceos23.spring_boot.cgv.repository.store.StorePurchaseRepository;
import com.ceos23.spring_boot.cgv.repository.user.UserRepository;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class StorePurchaseService {

    private final CinemaMenuStockRepository cinemaMenuStockRepository;
    private final StorePurchaseRepository storePurchaseRepository;
    private final UserRepository userRepository;
    private final AuditLogService auditLogService;
    private final BusinessMetricRecorder businessMetricRecorder;

    @CacheEvict(cacheNames = CacheNames.STORE_MENUS, key = "#request.cinemaId()")
    public void purchase(Long userId, StorePurchaseRequest request) {
        long startTime = System.currentTimeMillis();

        try {
            User user = findUserById(userId);
            CinemaMenuStock stock = findCinemaMenuStock(request);

            stock.decreaseStock(request.quantity());
            StorePurchase purchase = new StorePurchase(request.quantity(), user, stock);
            storePurchaseRepository.save(purchase);

            auditLogService.info(
                    "store_purchase_completed",
                    Map.of(
                            "userId", userId,
                            "cinemaId", request.cinemaId(),
                            "storeMenuId", request.storeMenuId(),
                            "quantity", request.quantity(),
                            "purchaseId", safeId(purchase.getId()),
                            "remainingStock", stock.getStockQuantity()
                    )
            );
            businessMetricRecorder.recordStorePurchaseEvent("success", System.currentTimeMillis() - startTime);
        } catch (RuntimeException exception) {
            String errorCode = exception instanceof NotFoundException notFoundException
                    ? notFoundException.getErrorCode().getCode()
                    : exception instanceof BadRequestException badRequestException
                    ? badRequestException.getErrorCode().getCode()
                    : ErrorCode.INTERNAL_SERVER_ERROR.getCode();

            auditLogService.warn(
                    "store_purchase_failed",
                    Map.of(
                            "userId", userId,
                            "cinemaId", request.cinemaId(),
                            "storeMenuId", request.storeMenuId(),
                            "quantity", request.quantity(),
                            "reason", errorCode
                    )
            );
            businessMetricRecorder.recordStorePurchaseEvent("failure", System.currentTimeMillis() - startTime);
            throw exception;
        }
    }

    private User findUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.USER_NOT_FOUND));
    }

    private CinemaMenuStock findCinemaMenuStock(StorePurchaseRequest request) {
        return cinemaMenuStockRepository
                .findByCinemaIdAndStoreMenuIdWithPessimisticLock(
                        request.cinemaId(),
                        request.storeMenuId()
                )
                .orElseThrow(() -> new NotFoundException(ErrorCode.STORE_MENU_STOCK_NOT_FOUND));
    }

    private Long safeId(Long id) {
        return id == null ? -1L : id;
    }
}
