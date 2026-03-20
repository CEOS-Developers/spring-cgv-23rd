package com.ceos23.spring_boot.controller;

import com.ceos23.spring_boot.domain.Item;
import com.ceos23.spring_boot.service.ItemService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/items")
public class ItemController {

    private final ItemService itemService;

    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    // 1️⃣ CREATE
    @PostMapping("/")
    public Item createItem(@RequestBody Item item) {
        return itemService.save(item);
    }

    // 2️⃣ READ ALL
    @GetMapping("/")
    public List<Item> getAllItems() {
        return itemService.findAll();
    }

    // 3️⃣ READ ONE
    @GetMapping("/{id}")
    public Item getItem(@PathVariable Long id) {
        return itemService.findById(id);
    }

    // 4️⃣ DELETE
    @DeleteMapping("/{id}")
    public void deleteItem(@PathVariable Long id) {
        itemService.delete(id);
    }
}