package com.ibra.tacticalrpg.skill;

import com.ibra.tacticalrpg.entities.Entity;

@FunctionalInterface
public interface SkillEffect {
    void apply(Entity source, Entity target);
}
