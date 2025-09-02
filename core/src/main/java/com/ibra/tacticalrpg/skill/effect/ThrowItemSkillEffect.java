package com.ibra.tacticalrpg.skill.effect;

import com.ibra.tacticalrpg.entities.Entity;

public class ThrowItemSkillEffect implements SkillEffect {

    @Override
    public void apply(Entity source, Entity target) {
        System.out.println("ThrowItem Skill Effect");
        /* TODO - show list of items from the SharedInventory
         * If target is in range (range from the skill), use item on target
         */
    }
}
