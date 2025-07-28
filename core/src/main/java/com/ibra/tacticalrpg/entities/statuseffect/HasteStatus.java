package com.ibra.tacticalrpg.entities.statuseffect;

import com.ibra.tacticalrpg.entities.EntityStats;

public class HasteStatus extends BaseStatusEffect {
    private final int speedBoost;

    public HasteStatus(int duration, int speedBoost) {
        super(StatusEffectType.HASTE, "Haste", duration, speedBoost);
        this.speedBoost = speedBoost;
    }

    @Override
    public void apply(EntityStats stats) {
        //Haste is applied once and removed after duration ends
        //Speed bonus should be managed by the EntityStats class
        System.out.println("Haste active. Speed Bonus: " + speedBoost);
        update();
    }

    public int getSpeedBoost() {
        return isExpired() ? 0 : speedBoost; // Return speed boost only if not expired
    }
}
