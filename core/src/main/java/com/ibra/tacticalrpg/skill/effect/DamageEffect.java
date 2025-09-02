package com.ibra.tacticalrpg.skill.effect;

import com.ibra.tacticalrpg.entities.Entity;

public class DamageEffect implements SkillEffect {
    private final int baseDamage;

    public DamageEffect(int baseDamage) {
        this.baseDamage = baseDamage;
    }

    @Override
    public void apply(Entity source, Entity target) {
        int attack = source.getFinalAttack();
        int defense = target.getFinalDefense();
        int damage = Math.max(1, baseDamage + attack - defense);

        target.getStats().setCurrentHp(target.getStats().getCurrentHp() - damage);

        System.out.println(source.getName() + " dealt " + damage + " damage to " + target.getName());
    }
}
