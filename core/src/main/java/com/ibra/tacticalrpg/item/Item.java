package com.ibra.tacticalrpg.item;

import com.ibra.tacticalrpg.entities.Entity;
import com.ibra.tacticalrpg.entities.EntityStats;

public abstract class Item {
    protected final String name;
    protected final String description;

    public Item(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public abstract void use(EntityStats stats, Entity target);

    @Override
    public String toString() {
        return "Item{" +
            "name='" + name + '\'' +
            ", description='" + description + '\'' +
            '}';
    }
}
