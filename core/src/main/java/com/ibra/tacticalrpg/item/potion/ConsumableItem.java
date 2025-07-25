package com.ibra.tacticalrpg.item.potion;

import com.ibra.tacticalrpg.entities.Entity;
import com.ibra.tacticalrpg.item.ItemType;
import com.ibra.tacticalrpg.item.Item;

public abstract class ConsumableItem extends Item {

    public ConsumableItem(String name, String description) {
        super(name, description, ItemType.CONSUMABLE);
    }

    public abstract void use(Entity target);
}
