package com.ibra.tacticalrpg.skill.effect;

import com.ibra.tacticalrpg.entities.Entity;

@FunctionalInterface
public interface SkillEffect {
    void apply(Entity source, Entity target);
}
