package com.ibra.tacticalrpg.item.potion;

import com.ibra.tacticalrpg.entities.Entity;
import com.ibra.tacticalrpg.entities.EntityStats;
import com.ibra.tacticalrpg.item.Item;

public class HealingPotion extends Item {

    private int healingAmount;

    public HealingPotion() {
        super("Healing Potion", "Restores 5 HP to an ally.");
        this.healingAmount = 5;
    }

    @Override
    public void use(EntityStats stats, Entity target) {
        int currentHp = target.getStats().getCurrentHp();
        target.getStats().setCurrentHp(currentHp + healingAmount);
        System.out.println("Used Healing Potion on "
            + target.getName() + ", restoring " + healingAmount + " HP.");
    }
}
