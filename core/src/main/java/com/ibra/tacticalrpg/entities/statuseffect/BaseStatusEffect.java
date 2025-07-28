package com.ibra.tacticalrpg.entities.statuseffect;

public abstract class BaseStatusEffect implements StatusEffect {
    protected int duration;
    protected int intensity;
    protected final StatusEffectType type;
    protected final String name;
    protected boolean stackable;

    public BaseStatusEffect(StatusEffectType type, String name, int duration, int intensity) {
        this.type = type;
        this.name = name;
        this.duration = Math.max(1, duration);
        this.intensity = Math.max(1, intensity);
        this.stackable = false;
    }

    @Override
    public boolean isExpired() {
        return duration <= 0;
    }

    @Override
    public void update() {
        if(duration > 0) {
            duration--;
        }
    }

    @Override
    public StatusEffectType getType() {
        return type;
    }

    @Override
    public int getDuration() {
        return duration;
    }

    @Override
    public boolean canStack() {
        return stackable;
    }

    @Override
    public String getDescription() {
        return name + " - Duration: " + duration + ", Intensity: " + intensity;
    }

    @Override
    public StatusEffect merge(StatusEffect other) {
        if(!canStack() || other.getType() != this.type) {
            return this;
        }

        BaseStatusEffect otherBase = (BaseStatusEffect) other;
        this.duration = Math.max(this.duration, otherBase.duration);
        this.intensity += otherBase.intensity;
        return this;
    }
}
