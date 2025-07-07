package com.ibra.tacticalrpg.skill;

import com.ibra.tacticalrpg.entities.Entity;

public class Skill {
    private final String name;
    private final String decscription;
    private final TargetType targetType;
    private final SkillEffect effect;

    public Skill(String name, String description, TargetType targetType, SkillEffect effect) {
        this.name = name;
        this.decscription = description;
        this.targetType = targetType;
        this.effect = effect;
    }

    public void applyEffect(Entity source, Entity target) {
        effect.apply(source, target);
    }

    public String getDecscription() {
        return decscription;
    }

    public SkillEffect getEffect() {
        return effect;
    }

    public String getName() {
        return name;
    }

    public TargetType getTargetType() {
        return targetType;
    }
}
