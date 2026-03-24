package com.ceos23.spring_boot.controller;

import com.ceos23.spring_boot.domain.Item;
import com.ceos23.spring_boot.dto.ItemRequest;
import com.ceos23.spring_boot.dto.ItemResponse;
import com.ceos23.spring_boot.repository.ItemRepository;
import com.ceos23.spring_boot.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemRepository itemRepository;

    // 🔥 CREATE
    @PostMapping
    public ItemResponse createItem(@RequestBody ItemRequest request) {
        Item item = request.toEntity();
        Item saved = itemRepository.save(item);
        return ItemResponse.from(saved);
    }

    // 🔥 GET ALL
    @GetMapping
    public List<ItemResponse> getAllItems() {
        return itemRepository.findAll()
                .stream()
                .map(ItemResponse::from)
                .toList();
    }

    // 🔥 GET ONE
    @GetMapping("/{id}")
    public ItemResponse getItem(@PathVariable Long id) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Item not found"));
        return ItemResponse.from(item);
    }
}