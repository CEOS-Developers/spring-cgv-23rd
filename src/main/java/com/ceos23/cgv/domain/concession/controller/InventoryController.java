package com.ceos23.cgv.domain.concession.controller;

import com.ceos23.cgv.domain.concession.dto.InventoryResponse;
import com.ceos23.cgv.domain.concession.dto.InventoryUpdateRequest;
import com.ceos23.cgv.domain.concession.entity.Inventory;
import com.ceos23.cgv.domain.concession.service.InventoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/inventories")
@RequiredArgsConstructor
@Tag(name = "Inventory API", description = "지점별 매점 상품 재고 관리 API")
public class InventoryController {

    private final InventoryService inventoryService;

    @PostMapping("/update")
    @Operation(summary = "재고 업데이트", description = "특정 지점의 상품 재고를 추가하거나 차감합니다. 재고는 최소 1개 이상이어야 합니다.")
    public ResponseEntity<InventoryResponse> updateInventory(@RequestBody InventoryUpdateRequest request) {
        Inventory inventory = inventoryService.updateInventory(request);
        return ResponseEntity.ok(InventoryResponse.from(inventory));
    }

    @GetMapping("/cinema/{cinemaId}")
    @Operation(summary = "지점별 재고 조회", description = "극장 ID를 통해 해당 지점의 모든 상품 재고 목록을 조회합니다.")
    public ResponseEntity<List<InventoryResponse>> getInventoriesByCinema(@PathVariable Long cinemaId) {
        List<InventoryResponse> responses = inventoryService.getInventoriesByCinemaId(cinemaId).stream()
                .map(InventoryResponse::from)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }
}