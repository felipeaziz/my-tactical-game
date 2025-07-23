package com.ibra.tacticalrpg.job;

import com.ibra.tacticalrpg.entities.EntityStats;
import com.ibra.tacticalrpg.skill.Skill;
import com.ibra.tacticalrpg.skill.TargetType;

public class Apprentice extends Job {

    public Apprentice() {
        super("Apprentice",
            "A starting job for all adventures. The foundation of many advanced classes.");
    }

    @Override
    public EntityStats applyInitialStats() {
        return new EntityStats(10, 1, 0, 3, 4, 1, 3);
    }

    @Override
    public void applyInitialSkills(EntityStats stats) {
        this.skills.add(new Skill("Watch", "Spend your turn watching and learning.",
            TargetType.SELF, null));
        // TODO - when using this skill, the entity should gain a small amount of experience or insight, but not take any action.
    }
}
