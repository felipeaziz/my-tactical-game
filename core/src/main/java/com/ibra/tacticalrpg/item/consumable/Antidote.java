package com.ibra.tacticalrpg.item.consumable;

import com.ibra.tacticalrpg.entities.Entity;
import com.ibra.tacticalrpg.entities.statuseffect.StatusEffectType;

public class Antidote extends ConsumableItem {

    public Antidote() {
        super("Antidote", "Cure poison.");;
    }

    @Override
    public void use(Entity target) {
        target.getStatusEffectManager().removeEffect(StatusEffectType.POISON);
        System.out.println("Used Antidote on " + target.getName() + ", curing poison.");
    }
}
