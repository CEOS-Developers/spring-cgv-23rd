package com.ceos23.spring_boot.cgv.controller.store;

import com.ceos23.spring_boot.cgv.dto.store.StorePurchaseRequest;
import com.ceos23.spring_boot.cgv.global.security.CustomUserDetails;
import com.ceos23.spring_boot.cgv.service.store.StorePurchaseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/store")
@RequiredArgsConstructor
public class StorePurchaseController {

    private final StorePurchaseService storePurchaseService;

    @PostMapping("/purchases")
    public ResponseEntity<Void> purchase(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody @Valid StorePurchaseRequest request
    ) {
        storePurchaseService.purchase(userDetails.getUserId(), request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
