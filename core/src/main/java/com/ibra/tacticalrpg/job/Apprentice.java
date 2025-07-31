package com.ibra.tacticalrpg.job;

import com.ibra.tacticalrpg.entities.EntityStats;
import com.ibra.tacticalrpg.entities.statuseffect.DoubleXpStatusEffect;
import com.ibra.tacticalrpg.item.equipment.EquipmentSlot;
import com.ibra.tacticalrpg.skill.Skill;
import com.ibra.tacticalrpg.skill.effect.StatusEffectApplication;
import com.ibra.tacticalrpg.skill.TargetType;

import java.util.ArrayList;
import java.util.List;

public class Apprentice extends Job {

    public Apprentice() {
        super("Apprentice",
            "A starting job for all adventures. The foundation of many advanced classes.");
        levelUpBonus = new EntityStats(2, 0, 0, 1, 0, 0, 0);
        allowedEquipmentSlots = new ArrayList<>();
        allowedEquipmentSlots.add(EquipmentSlot.WEAPON);
        allowedEquipmentSlots.add(EquipmentSlot.BOOTS);
        allowedEquipmentSlots.add(EquipmentSlot.ACCESSORY_1);
    }

    @Override
    public EntityStats applyInitialStats() {
        return new EntityStats(10, 1, 0, 3, 4, 1, 3);
    }

    @Override
    public void applyInitialSkills(EntityStats stats) {
        this.skills.add(new Skill("Watch", "Spend your turn watching and learning.",
            TargetType.SELF, new StatusEffectApplication(new DoubleXpStatusEffect(1))));
    }
}
