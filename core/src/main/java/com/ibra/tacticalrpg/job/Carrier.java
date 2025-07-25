package com.ibra.tacticalrpg.job;

import com.ibra.tacticalrpg.entities.EntityStats;
import com.ibra.tacticalrpg.skill.Skill;
import com.ibra.tacticalrpg.skill.TargetType;

import java.util.List;

import static com.ibra.tacticalrpg.item.equipment.EquipmentSlot.*;

public class Carrier extends Job {

    public Carrier() {
        super("Carrier",
            "A utility-oriented class specialized in carrying and using items effectively.");
        this.levelUpBonus = new EntityStats(1, 0, 0, 2, 0, 0, 0);
        this.allowedEquipmentSlots = List.of(WEAPON, BOOTS, ACCESSORY_1);
    }

    @Override
    public EntityStats applyInitialStats() {
        return new EntityStats(12, 1, 0, 3, 3, 1, 2);
    }

    @Override
    public void applyInitialSkills(EntityStats stats) {
        //TODO - If the player has this skill, they can thrown and use an item on any entity in a certain range.
        this.skills.add(new Skill(
            "Throw Item",
            "Use an item from your inventory on a selected target on distance.",
            TargetType.ANY,
            null));
    }
}
