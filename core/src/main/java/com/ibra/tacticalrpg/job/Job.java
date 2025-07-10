package com.ibra.tacticalrpg.job;

import com.badlogic.gdx.graphics.Texture;
import com.ibra.tacticalrpg.entities.EntityStats;
import com.ibra.tacticalrpg.skill.Skill;

import java.util.ArrayList;
import java.util.List;

public abstract class Job {
    protected final String name;
    protected final String description;
    protected List<Skill> skills;
    protected Texture texture;

    public Job(String name, String description) {
        this.name = name;
        this.description = description;
        this.skills = new ArrayList<>();
        this.texture = new Texture("job/" + name.toLowerCase() + ".png"); // Assuming textures are named after the job
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

    public Texture getTexture() {
        return texture;
    }

    public void setTexture(Texture texture) {
        this.texture = texture;
    }

    public abstract EntityStats applyInitialStats();
    public abstract void applyInitialSkills(EntityStats stats);
}
