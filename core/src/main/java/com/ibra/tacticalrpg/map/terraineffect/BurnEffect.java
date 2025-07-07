package com.ibra.tacticalrpg.map.terraineffect;

import com.ibra.tacticalrpg.entities.Entity;

public class BurnEffect implements TerrainEffect {
    @Override
    public void applyEffect(Entity entity) {
        if (entity.isAlive()) {
            int burnDamage = 2; // Exemplo de dano de queimadura
            entity.getStats().setCurrentHp(entity.getStats().getCurrentHp() - burnDamage);
            System.out.println(entity.getName() + " sofreu " + burnDamage + " de dano por queimadura.");
        } else {
            System.out.println(entity.getName() + " já está morto e não pode sofrer mais dano.");
        }
    }

    @Override
    public void removeEffect(Entity entity) {

    }
}
