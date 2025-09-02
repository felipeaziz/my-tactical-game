package com.ibra.tacticalrpg.entities.statuseffect;

import com.ibra.tacticalrpg.entities.EntityStats;

public class DoubleXpStatusEffect extends BaseStatusEffect {

    public DoubleXpStatusEffect(int duration) {
        super(StatusEffectType.DOUBLE_XP, "Double XP", duration, 1);
    }

    @Override
    public void apply(EntityStats stats) {
        System.out.println("Vai ganhar o dobro de XP no turno");
        //TODO - doubles the amount of XP earned by the entity
    }
}
