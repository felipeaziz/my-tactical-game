package com.ibra.tacticalrpg.skill.effect;

import com.ibra.tacticalrpg.entities.Entity;
import com.ibra.tacticalrpg.entities.statuseffect.StatusEffect;

public class StatusEffectApplication implements SkillEffect {
    private final StatusEffect statusEffect;

    public StatusEffectApplication(StatusEffect statusEffect) {
        this.statusEffect = statusEffect;
    }

    @Override
    public void apply(Entity source, Entity target) {
        target.getStatusEffectManager().addStatusEffect(statusEffect);
        System.out.println(target.getName() + " is affected by " + statusEffect.getType());
    }
}
