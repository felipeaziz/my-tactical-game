package com.ibra.tacticalrpg.skill;

import com.ibra.tacticalrpg.entities.Entity;
import com.ibra.tacticalrpg.skill.effect.SkillEffect;

public class Skill {
    private final String name;
    private final String description;
    private final TargetType targetType;
    private final SkillEffect effect;
    private final int range;
    private final int cooldown;
    protected int currentCooldown;
    private final int requiredLevel;

    public Skill(String name,
                 String description,
                 TargetType targetType,
                 SkillEffect effect,
                 int range,
                 int cooldown,
                 int requiredLevel) {
        this.cooldown = cooldown;
        this.description = description;
        this.effect = effect;
        this.name = name;
        this.range = range;
        this.targetType = targetType;
        this.currentCooldown = 0;
        this.requiredLevel = requiredLevel;
    }

    public Skill(String name, String description, TargetType targetType, SkillEffect effect) {
        this(name, description, targetType, effect, 1, 0, 1);
    }

    public boolean canUse(Entity user) {
        return currentCooldown == 0
            && user.getStatusEffectManager().canAct()
            && user.getLevel() >= requiredLevel;
    }

    public void use(Entity source, Entity target) {
        if (!canUse(source)) {
            System.out.println("Cannot use skill: " + name);
            return;
        }
        effect.apply(source, target);
        currentCooldown = cooldown;
        System.out.println(source.getName() + " used skill: " + name + " on " + target.getName());
    }

    public void updateCooldown() {
        if (currentCooldown > 0) {
            currentCooldown--;
        }
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public TargetType getTargetType() {
        return targetType;
    }

    public SkillEffect getEffect() {
        return effect;
    }

    public int getRange() {
        return range;
    }

    public int getCurrentCooldown() {
        return currentCooldown;
    }

    public boolean isOnCooldown() {
        return currentCooldown > 0;
    }

    public int getRequiredLevel() {
        return requiredLevel;
    }

    public int getCooldown() {
        return cooldown;
    }
}
