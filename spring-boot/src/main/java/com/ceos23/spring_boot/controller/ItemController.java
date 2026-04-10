package com.ceos23.spring_boot.controller;

import com.ceos23.spring_boot.domain.Item;
import com.ceos23.spring_boot.dto.ItemRequest;
import com.ceos23.spring_boot.dto.ItemResponse;
import com.ceos23.spring_boot.global.response.SuccessResponse;
import com.ceos23.spring_boot.service.ItemService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/items")
@Tag(name = "Item", description = "아이템 관련 API")
public class ItemController {

    private final ItemService itemService;

    @Operation(summary = "아이템 생성", description = "새로운 아이템을 생성합니다.")
    @PostMapping
    public SuccessResponse<ItemResponse> createItem(@RequestBody ItemRequest request) {
        Item item = request.toEntity();
        Item saved = itemService.save(item);
        return new SuccessResponse<>(200, "SUCCESS", ItemResponse.from(saved));
    }

    @Operation(summary = "아이템 전체 조회")
    @GetMapping
    public SuccessResponse<List<ItemResponse>> getAllItems() {
        List<ItemResponse> items = itemService.findAll()
                .stream()
                .map(ItemResponse::from)
                .toList();

        return new SuccessResponse<>(200, "SUCCESS", items);
    }

    @Operation(summary = "아이템 단건 조회")
    @GetMapping("/{itemId}")
    public SuccessResponse<ItemResponse> getItem(@PathVariable Long itemId) {
        Item item = itemService.findById(itemId);
        return new SuccessResponse<>(200, "SUCCESS", ItemResponse.from(item));
    }
}