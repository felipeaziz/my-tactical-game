package com.ibra.tacticalrpg.item.equipment;

import com.ibra.tacticalrpg.item.ItemType;

//TODO - maybe a weapon should not be an item.
public class Weapon extends Equipment {
    private final int attackPower;
    private final int range;

    public Weapon(String name, String description, int attackPower, int range) {
        super(name, description, ItemType.WEAPON, EquipmentSlot.WEAPON);
        this.attackPower = attackPower;
        this.range = range;
        this.statBonuses.put("attack", attackPower);
    }

    public int getAttackPower() {
        return attackPower;
    }

    public int getRange() {
        return range;
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
