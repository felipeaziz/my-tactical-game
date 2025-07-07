package com.ibra.tacticalrpg.map.terraineffect;

import com.ibra.tacticalrpg.entities.Entity;
import com.ibra.tacticalrpg.entities.statuseffect.PoisonStatus;

public class PoisonEffect implements TerrainEffect {
    @Override
    public void applyEffect(Entity entity) {
        if (entity.isAlive()) {
            entity.getStats().applyStatusEffect(new PoisonStatus());
            System.out.println(entity.getName() + " está envenenado e sofre dano.");
        } else {
            System.out.println(entity.getName() + " já está morto e não pode sofrer mais dano.");
        }
    }

    @Override
    public void removeEffect(Entity entity) {

    }
}
