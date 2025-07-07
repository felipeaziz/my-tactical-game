package com.ibra.tacticalrpg.item;

import com.ibra.tacticalrpg.entities.EntityStats;

public class Weapon extends Item {
    private final int attackPower;
    private final int range;

    public Weapon(String name, String description, int attackPower, int range) {
        super(name, description);
        this.attackPower = attackPower;
        this.range = range;
    }

    public int getAttackPower() {
        return attackPower;
    }

    public int getRange() {
        return range;
    }

    @Override
    public void applyEffects(EntityStats stats) {
        stats.setAttack(attackPower);
        stats.setAttackRange(range);
    }

    @Override
    public String toString() {
        return "Weapon{" +
                "name='" + getName() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", attackPower=" + attackPower +
                ", range=" + range +
                '}';
    }
}
