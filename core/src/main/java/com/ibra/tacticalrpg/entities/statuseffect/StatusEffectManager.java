package com.ibra.tacticalrpg.entities.statuseffect;

import com.ibra.tacticalrpg.entities.EntityStats;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class StatusEffectManager {
    private List<StatusEffect> activeEffects;

    public StatusEffectManager() {
        this.activeEffects = new ArrayList<>();
    }

    public void addStatusEffect(StatusEffect newEffect) {
        for (int i = 0; i < activeEffects.size(); ++i) {
            StatusEffect existing = activeEffects.get(i);
            if (existing.getType().equals(newEffect.getType())) {
                if (existing.canStack()) {
                    activeEffects.set(i, existing.merge(newEffect));
                } else {
                    activeEffects.set(i, newEffect);
                }
                return;
            }
        }
        activeEffects.add(newEffect);
    }

    public void processEffects(EntityStats stats) {
        Iterator<StatusEffect> iterator = activeEffects.iterator();
        while (iterator.hasNext()) {
            StatusEffect effect = iterator.next();
            effect.apply(stats);
            if (effect.isExpired()) {
                iterator.remove();
                System.out.println("Status effect " + effect.getType() + "expired");
            }
        }
    }

    public boolean canAct() {
        return activeEffects.stream()
            .noneMatch(effect -> effect instanceof StunStatus && !effect.isExpired());
        //TODO - in the future, more effects may prevent actions
    }

    public int getAttackModifier() {
        int modifier = 0;
        for (StatusEffect effect : activeEffects) {
            //TODO - implement effects like rage or berserk to increase attack
        }
        return modifier;
    }

    public int getDefenseModifier() {
        int modifier = 0;
        for (StatusEffect effect : activeEffects) {
            //TODO - implement effects like shield or mage armor to increase defense
        }
        return modifier;
    }

    public int getSpeedModifier() {
        int modifier = 0;
        for (StatusEffect effect : activeEffects) {
            if (effect instanceof HasteStatus && !effect.isExpired()) {
                modifier += ((HasteStatus) effect).getSpeedBoost();
            }
            //TODO - implement other effects like slow to modify speed
        }
        return modifier;
    }

    public boolean hasEffect(StatusEffectType type) {
        return activeEffects.stream().anyMatch(effect -> effect.getType().equals(type));
    }

    public StatusEffect getEffect(StatusEffectType type) {
        return activeEffects.stream()
            .filter(effect -> effect.getType().equals(type))
            .findFirst()
            .orElse(null);
    }

    public void removeEffect(StatusEffectType type) {
        activeEffects.removeIf(effect -> effect.getType().equals(type));
    }

    public List<StatusEffect> getAllEffects() {
        return new ArrayList<>(activeEffects);
    }

    public void clearAllEffects() {
        activeEffects.clear();
    }
}
