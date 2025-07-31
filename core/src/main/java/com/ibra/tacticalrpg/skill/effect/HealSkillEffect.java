package com.ibra.tacticalrpg.skill.effect;

import com.ibra.tacticalrpg.entities.Entity;

public class HealSkillEffect implements SkillEffect {
    private final int healAmount;

    public HealSkillEffect(int healAmount) {
        this.healAmount = healAmount;
    }

    @Override
    public void apply(Entity source, Entity target) {
        int newHp = target.getStats().getCurrentHp() + healAmount;
        target.getStats().setCurrentHp(newHp);
        System.out.println(target.getName() + " recovered " + healAmount + " HP!");
    }
}
