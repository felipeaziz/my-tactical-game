package com.ibra.tacticalrpg.entities;

public class EntityStats {
    private int maxHp;
    private int currentHp;
    private int maxMp;
    private int currentMp;
    private int attack;
    private int defense;
    private int moveRange;
    private int attackRange;
    private int speed;

    public EntityStats(int maxHp, int attack, int defense, int maxMp, int moveRange, int attackRange, int speed) {
        this.maxHp = Math.max(1, maxHp);
        this.currentHp = this.maxHp;
        this.maxMp = Math.max(0, maxMp);
        this.currentMp = this.maxMp;
        this.attack = attack;
        this.defense = defense;
        this.moveRange = moveRange;
        this.attackRange = attackRange;
        this.speed = speed;
    }

    public int getStatusEffectModifier(String stat) {
        return 0;
    }

    public void applyLevelUpBonus(EntityStats bonus) {
        this.maxHp += bonus.getMaxHp();
        this.currentHp = Math.min(this.currentHp + bonus.getCurrentHp(), this.maxHp);
        this.maxMp += bonus.getMaxMp();
        this.currentMp = Math.min(this.currentMp + bonus.getCurrentMp(), this.maxMp);
        this.attack += bonus.getAttack();
        this.defense += bonus.getDefense();
        this.moveRange += bonus.getMoveRange();
        this.attackRange += bonus.getAttackRange();
        this.speed += bonus.getSpeed();
    }

    public void takeDamage(int damage) {
        currentHp = Math.max(0, currentHp - damage);
    }

    public void heal(int amount) {
        currentHp = Math.min(maxHp, currentHp + amount);
    }

    public int getMaxHp() {
        return maxHp;
    }

    public void setMaxHp(int maxHp) {
        this.maxHp = Math.max(1, maxHp);
        this.currentHp = Math.min(this.currentHp, this.maxHp);
    }

    public int getCurrentHp() {
        return currentHp;
    }

    public void setCurrentHp(int currentHp) {
        this.currentHp = Math.min(maxHp, Math.max(0, currentHp));
    }

    public int getMaxMp() {
        return maxMp;
    }

    public void setMaxMp(int maxMp) {
        this.maxMp = Math.max(0, maxMp);
        this.currentMp = Math.min(this.currentMp, this.maxMp);
    }

    public int getCurrentMp() {
        return currentMp;
    }

    public void setCurrentMp(int currentMp) {
        this.currentMp = Math.min(maxMp, Math.max(0, currentMp));
    }

    public int getAttack() {
        return attack;
    }

    public void setAttack(int attack) {
        this.attack = attack;
    }

    public int getDefense() {
        return defense;
    }

    public void setDefense(int defense) {
        this.defense = defense;
    }

    public int getMoveRange() {
        return moveRange;
    }

    public void setMoveRange(int moveRange) {
        this.moveRange = moveRange;
    }

    public int getAttackRange() {
        return attackRange;
    }

    public void setAttackRange(int attackRange) {
        this.attackRange = attackRange;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = Math.max(1, speed);
    }
}
