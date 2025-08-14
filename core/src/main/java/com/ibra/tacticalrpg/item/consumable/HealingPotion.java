package com.ibra.tacticalrpg.item.consumable;

import com.ibra.tacticalrpg.entities.Entity;

public class HealingPotion extends ConsumableItem {

    private final int healingAmount;

    public HealingPotion() {
        super("Healing Potion", "Restores 5 HP to an ally.");
        this.healingAmount = 5;
    }

    @Override
    public void use(Entity target) {
        int currentHp = target.getStats().getCurrentHp();
        target.getStats().setCurrentHp(currentHp + healingAmount);
        System.out.println("Used Healing Potion on "
            + target.getName() + ", restoring " + healingAmount + " HP.");
    }
}
