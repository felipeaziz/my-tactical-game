package com.ibra.tacticalrpg.job;

import com.ibra.tacticalrpg.entities.EntityStats;
import com.ibra.tacticalrpg.skill.Skill;
import com.ibra.tacticalrpg.skill.TargetType;

public class Carrier extends Job {

    public Carrier() {
        super("Carrier",
            "A utility-oriented class specialized in carrying and using items effectively.");
    }

    @Override
    public EntityStats applyInitialStats() {
        return new EntityStats(12, 1, 0, 3, 3, 1, 2);
    }

    @Override
    public void applyInitialSkills(EntityStats stats) {
        //TODO - If the player has this skill, they can thrown and use an item on any entity in a certain range.
        this.skills.add(new Skill(
            "Throw Item",
            "Use an item from your inventory on a selected target on distance.",
            TargetType.ANY,
            null));
    }
}
