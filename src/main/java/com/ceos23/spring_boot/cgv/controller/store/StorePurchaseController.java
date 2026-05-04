package com.ceos23.spring_boot.cgv.controller.store;

import com.ceos23.spring_boot.cgv.dto.store.StoreMenuResponse;
import com.ceos23.spring_boot.cgv.dto.store.StorePurchaseRequest;
import com.ceos23.spring_boot.cgv.dto.store.StorePurchaseResponse;
import com.ceos23.spring_boot.cgv.global.security.CustomUserDetails;
import com.ceos23.spring_boot.cgv.service.store.StorePurchaseService;
import com.ceos23.spring_boot.cgv.service.store.StoreQueryService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/store")
@RequiredArgsConstructor
public class StorePurchaseController {

    private final StorePurchaseService storePurchaseService;
    private final StoreQueryService storeQueryService;

    @GetMapping("/menus")
    public ResponseEntity<List<StoreMenuResponse>> getStoreMenus(@RequestParam Long cinemaId) {
        List<StoreMenuResponse> responses = storeQueryService.getStoreMenus(cinemaId).stream()
                .map(StoreMenuResponse::from)
                .toList();

        return ResponseEntity.ok(responses);
    }

    @PostMapping("/purchases")
    public ResponseEntity<Void> purchase(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody @Valid StorePurchaseRequest request
    ) {
        storePurchaseService.purchase(userDetails.getUserId(), request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/purchases")
    public ResponseEntity<List<StorePurchaseResponse>> getPurchases(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        List<StorePurchaseResponse> responses = storeQueryService.getPurchaseHistory(userDetails.getUserId())
                .stream()
                .map(StorePurchaseResponse::from)
                .toList();

        return ResponseEntity.ok(responses);
    }
}
