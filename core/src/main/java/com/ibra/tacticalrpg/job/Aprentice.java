package com.ibra.tacticalrpg.job;

import com.ibra.tacticalrpg.entities.EntityStats;
import com.ibra.tacticalrpg.skill.Skill;
import com.ibra.tacticalrpg.skill.TargetType;

import java.util.Collections;
import java.util.List;

public class Aprentice extends Job {

    public Aprentice() {
        super("Aprentice",
            "A starting job for all adventures. The foundation of many advanced classes.");
    }

    @Override
    public void applyInitialStats(EntityStats stats) {
        stats.setMaxHp(5);
        stats.setAttack(2);
        stats.setAttackRange(1);
        stats.setDefense(0);
        stats.setSpeed(2);
        stats.setMoveRange(3);
    }

    @Override
    public List<Skill> getSkills(EntityStats stats) {
        return Collections.singletonList(new Skill("Watch", "Spend your turn watching and learning.",
            TargetType.NONE, null));
    }
}
