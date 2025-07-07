package com.ibra.tacticalrpg.entities.statuseffect;

import com.ibra.tacticalrpg.entities.EntityStats;

public interface StatusEffect {
    void apply(EntityStats stats);
    boolean isExpired();
    void update();
}
