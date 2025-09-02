package com.ibra.tacticalrpg.skill;

public enum TargetType {
    SELF,
    ALLY,
    ENEMY,
    ANY,
    AREA,
    LINE,
    ALL_ALLIES,
    ALL_ENEMIES,
    ALL_ENTITIES,
    NONE; // Used for skills that do not require a target, like passive skills
}
