package com.ceos23.spring_boot.service;

import com.ceos23.spring_boot.domain.Item;
import com.ceos23.spring_boot.repository.ItemRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ItemService {

    private final ItemRepository itemRepository;

    public ItemService(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    // CREATE
    public Item save(Item item) {
        return itemRepository.save(item);
    }

    // READ ALL
    public List<Item> findAll() {
        return itemRepository.findAll();
    }

    // READ ONE
    public Item findById(Long id) {
        return itemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Item not found"));
    }

    // DELETE
    public void delete(Long id) {
        itemRepository.deleteById(id);
    }
}