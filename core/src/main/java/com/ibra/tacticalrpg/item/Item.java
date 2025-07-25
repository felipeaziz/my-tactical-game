package com.ibra.tacticalrpg.item;

public abstract class Item {
    public static final int MAX_ITEM_STACK = 99;

    protected final String name;
    protected final String description;
    protected final ItemType type;
    protected int value;

    public Item(String name, String description, ItemType type) {
        this.name = name;
        this.description = description;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public ItemType getType() {
        return type;
    }

    @Override
    public String toString() {
        return "Item{" +
            "name='" + name + '\'' +
            ", description='" + description + '\'' +
            '}';
    }
}
