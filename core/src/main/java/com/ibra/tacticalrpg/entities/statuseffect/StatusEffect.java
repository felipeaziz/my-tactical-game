package com.ibra.tacticalrpg.entities.statuseffect;

import com.ibra.tacticalrpg.entities.EntityStats;

public interface StatusEffect {
    void apply(EntityStats stats);
    boolean isExpired();
    void update();
    StatusEffectType getType();
    int getDuration();
    boolean canStack(); // Indica se o efeito pode se acumular
    StatusEffect merge(StatusEffect other); // Para combinar efeitos do mesmo tipo
    String getDescription();
}
