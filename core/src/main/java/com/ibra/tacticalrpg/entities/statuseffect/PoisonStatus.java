package com.ibra.tacticalrpg.entities.statuseffect;

import com.ibra.tacticalrpg.entities.EntityStats;

public class PoisonStatus implements StatusEffect {
    private final int damagePerTurn;

    public PoisonStatus() {
        damagePerTurn = 1;
    }

    public PoisonStatus(int damagePerTurn) {
        this.damagePerTurn = Math.max(1, damagePerTurn);
    }

    @Override
    public void apply(EntityStats stats) {
        stats.setCurrentHp(stats.getCurrentHp() - damagePerTurn);
        System.out.println("Poison applied: " + damagePerTurn + " damage.");
    }

    @Override
    public boolean isExpired() {
        return false;
    }

    @Override
    public void update() {
        // Only can be cured by antidote or healing
    }
}
