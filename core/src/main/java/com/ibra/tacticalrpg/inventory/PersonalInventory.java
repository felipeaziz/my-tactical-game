package com.ibra.tacticalrpg.inventory;

import com.ibra.tacticalrpg.entities.Entity;
import com.ibra.tacticalrpg.item.equipment.Equipment;
import com.ibra.tacticalrpg.item.equipment.EquipmentSlot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PersonalInventory extends Inventory {
    private static final int MAX_ITEMS_SLOTS = 10;

    private Map<EquipmentSlot, Equipment> equipedItems;
    private List<Equipment> unequippedItems;

    public PersonalInventory() {
        this.equipedItems = new HashMap<>();
        this.unequippedItems = new ArrayList<>();
    }

    public boolean equipItem(Equipment item, Entity owner) {
        if (!owner.getJob().canEquip(item)) {
            return false;
        }
        Equipment currentEquipped = equipedItems.get(item.getSlot());
        if (currentEquipped != null) {
            unequippedItems(item.getSlot());
        }

        equipedItems.put(item.getSlot(), item);
        unequippedItems.remove(item);
        return true;
    }

    public Equipment unequippedItems(EquipmentSlot slot) {
        Equipment item = equipedItems.remove(slot);
        if (item != null && unequippedItems.size() < MAX_ITEMS_SLOTS) {
            unequippedItems.add(item);
        }
        return item;
    }

    public int getEquipmentBonus(String stat) {
        return equipedItems.values().stream()
            .mapToInt(equipment -> equipment.getStatBonus(stat))
            .sum();
    }
}
