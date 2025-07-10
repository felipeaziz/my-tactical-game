package com.ibra.tacticalrpg.job;

import com.ibra.tacticalrpg.entities.EntityStats;
import com.ibra.tacticalrpg.skill.Skill;

import java.util.ArrayList;
import java.util.List;

public abstract class Job {
    protected final String name;
    protected final String description;
    protected List<Skill> skills;

    public Job(String name, String description) {
        this.name = name;
        this.description = description;
        this.skills = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public List<Skill> getSkills() {
        return skills;
    }

    public void addSkill(Skill skill) {
        if (skill != null && !skills.contains(skill)) {
            skills.add(skill);
        }
    }

    public abstract EntityStats applyInitialStats();
    public abstract void applyInitialSkills(EntityStats stats);
}
