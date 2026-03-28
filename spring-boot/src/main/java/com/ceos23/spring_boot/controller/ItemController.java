package com.ceos23.spring_boot.controller;

import com.ceos23.spring_boot.domain.Item;
import com.ceos23.spring_boot.dto.ItemRequest;
import com.ceos23.spring_boot.dto.ItemResponse;
import com.ceos23.spring_boot.repository.ItemRepository;
import com.ceos23.spring_boot.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/items")
public class ItemController {

    private final ItemService itemService;

    @PostMapping
    public ItemResponse createItem(@RequestBody ItemRequest request) {
        Item item = request.toEntity();
        Item saved = itemService.save(item);
        return ItemResponse.from(saved);
    }

    @GetMapping
    public List<ItemResponse> getAllItems() {
        return itemService.findAll()
                .stream()
                .map(ItemResponse::from)
                .toList();
    }

    @GetMapping("/{id}")
    public ItemResponse getItem(@PathVariable Long id) {
        Item item = itemService.findById(id);
        return ItemResponse.from(item);
    }
}