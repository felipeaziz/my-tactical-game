package com.ibra.tacticalrpg.job;

import com.ibra.tacticalrpg.entities.EntityStats;
import com.ibra.tacticalrpg.skill.Skill;

import java.util.List;

public abstract class Job {
    protected final String name;
    protected final String description;

    public Job(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public abstract void applyInitialStats(EntityStats stats);
    public abstract List<Skill> getSkills(EntityStats stats);
}
