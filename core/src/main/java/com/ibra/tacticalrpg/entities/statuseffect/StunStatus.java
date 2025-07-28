package com.ibra.tacticalrpg.entities.statuseffect;

import com.ibra.tacticalrpg.entities.EntityStats;

public class StunStatus extends BaseStatusEffect {
    public StunStatus(int duration) {
        super(StatusEffectType.STUN, "Stun", duration, 1);
    }

    @Override
    public void apply(EntityStats stats) {
        System.out.println("Entity is stunned and cannot act this turn");
        update();
    }

    public boolean canAct() {
        return isExpired();
    }
}
