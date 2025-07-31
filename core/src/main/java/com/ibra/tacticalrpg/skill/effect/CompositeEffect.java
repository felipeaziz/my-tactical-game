package com.ibra.tacticalrpg.skill.effect;

import com.ibra.tacticalrpg.entities.Entity;

public class CompositeEffect implements SkillEffect {
    private final SkillEffect[] effects;

    public CompositeEffect(SkillEffect... effects) {
        this.effects = effects;
    }

    @Override
    public void apply(Entity source, Entity target) {
        for(SkillEffect effect : effects) {
            effect.apply(source, target);
        }
    }
}
