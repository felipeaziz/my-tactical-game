package com.ibra.tacticalrpg.inventory;

import java.util.HashMap;

public class SharedInventory extends Inventory {
    private static SharedInventory instance;

    public static SharedInventory getInstance() {
        if (instance == null) {
            instance = new SharedInventory();
        }
        return instance;
    }

    private SharedInventory() {
        // Initialize the inventory
        this.items = new HashMap<>();
    }
}
