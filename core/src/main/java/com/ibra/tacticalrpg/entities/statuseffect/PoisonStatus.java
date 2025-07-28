package com.ibra.tacticalrpg.entities.statuseffect;

import com.ibra.tacticalrpg.entities.EntityStats;

public class PoisonStatus extends BaseStatusEffect {

    public PoisonStatus(int duration, int damagePerTurn) {
        super(StatusEffectType.POISON, "Poison", duration, Math.max(1, damagePerTurn));
        this.stackable = true;
    }

    public PoisonStatus(int damagePerTurn) {
        this(99, damagePerTurn); // Poison can be removed only by antidote or healing
    }

    public PoisonStatus() {
        this(1); // Default poison damage per turn
    }

    @Override
    public void apply(EntityStats stats) {
        stats.setCurrentHp(stats.getCurrentHp() - intensity);
        System.out.println("Poison applied: " + intensity + " damage.");
    }
}
