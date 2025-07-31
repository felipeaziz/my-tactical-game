package com.ibra.tacticalrpg.inventory;

import com.ibra.tacticalrpg.item.Item;
import com.ibra.tacticalrpg.item.ItemType;
import com.ibra.tacticalrpg.item.potion.ConsumableItem;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public abstract class Inventory {
    protected static final int MAX_ITEMS_SLOTS = 100;

    protected Map<Item, Integer> items;

    public Inventory() {
        this.items = new HashMap<>();
    }

    public boolean addItem(Item item, int quantity) {
        if (items.size() >= MAX_ITEMS_SLOTS) {
            return false; // Inventory is full
        }
        if (items.getOrDefault(item, 0) + quantity > Item.MAX_ITEM_STACK) {
            return false; // Exceeds max stack size
        }
        items.put(item, items.getOrDefault(item, 0) + quantity);
        return true;
    }

    public boolean removeItem(Item item, int quantity) {
        if (!items.containsKey(item) || items.get(item) < quantity) {
            return false; // Item not found or insufficient quantity
        }
        int newQuantity = items.get(item) - quantity;
        if (newQuantity <= 0) {
            items.remove(item); // Remove item if quantity is zero or less
        } else {
            items.put(item, newQuantity);
        }
        return true;
    }

    public boolean hasConsumableItem() {
        return items.keySet().stream().anyMatch(item -> item.getType() == ItemType.CONSUMABLE);
    }

    public List<ConsumableItem> getConsumableItems() {
        return items.keySet().stream()
            .filter(item -> item.getType() == ItemType.CONSUMABLE)
            .map(item -> (ConsumableItem) item)
            .collect(Collectors.toList());
    }
}
