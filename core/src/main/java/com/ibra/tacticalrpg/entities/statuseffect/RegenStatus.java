package com.ibra.tacticalrpg.entities.statuseffect;

import com.ibra.tacticalrpg.entities.EntityStats;

public class RegenStatus extends BaseStatusEffect {

    public RegenStatus(int duration, int healPerTurn) {
        super(StatusEffectType.REGEN, "Regenaration", duration, healPerTurn);
        this.stackable = true;
    }

    public RegenStatus(int healPerTurn) {
        this(5, healPerTurn);
    }

    public RegenStatus() {
        this(1); // Default regen heal per turn
    }

    @Override
    public void apply(EntityStats stats) {
        stats.setCurrentHp(Math.min(stats.getCurrentHp() + intensity, stats.getMaxHp()));
        System.out.println("Regen applied: " + intensity + " healed.");
        update();
    }
}
