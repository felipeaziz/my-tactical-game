package com.ibra.tacticalrpg.inventory;

public class SharedInventory extends Inventory {
    private static SharedInventory instance;

    public static SharedInventory getInstance() {
        if (instance == null) {
            instance = new SharedInventory();
        }
        return instance;
    }

    private SharedInventory() {
        super();
    }
}
