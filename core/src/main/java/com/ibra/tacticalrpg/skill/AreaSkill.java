package com.ibra.tacticalrpg.skill;

import com.ibra.tacticalrpg.entities.EnemyEntity;
import com.ibra.tacticalrpg.entities.Entity;
import com.ibra.tacticalrpg.entities.PlayerEntity;
import com.ibra.tacticalrpg.map.isometric.GameMap;
import com.ibra.tacticalrpg.map.isometric.Tile;
import com.ibra.tacticalrpg.skill.effect.SkillEffect;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.ibra.tacticalrpg.grid.IsometricGridUtils.getTilesInRange;

public class AreaSkill extends Skill {
    private final int areaRadius;
    private final boolean affectsAllies;
    private final boolean affectsEnemies;

    public AreaSkill(String name,
                     String description,
                     SkillEffect effect,
                     int range,
                     int cooldown,
                     int requiredLevel,
                     int areaRadius,
                     boolean affectsAllies,
                     boolean affectsEnemies) {
        super(name, description, TargetType.AREA, effect, range, cooldown, requiredLevel);
        this.areaRadius = areaRadius;
        this.affectsAllies = affectsAllies;
        this.affectsEnemies = affectsEnemies;
    }

    public void useOnArea(Entity source, Tile centerTile, GameMap gameMap) {
        if (!canUse(source)) {
            System.out.println("Cannot use area skill: " + getName());
            return;
        }

        List<Entity> entitiesInArea = getEntitiesInArea(centerTile, gameMap);
        for (Entity target : entitiesInArea) {
            if (shouldAffectEntity(source, target)) {
                getEffect().apply(source, target);
            }
        }

        currentCooldown = getCooldown();
    }

    private boolean shouldAffectEntity(Entity source, Entity target) {
        if (source == target) {
            return false;
        }
        boolean isAlly = (source instanceof PlayerEntity && target instanceof PlayerEntity)
            || (source instanceof EnemyEntity && target instanceof EnemyEntity);
        return (isAlly && affectsAllies) || (!isAlly && affectsEnemies);
    }

    private List<Entity> getEntitiesInArea(Tile centerTile, GameMap gameMap) {
        List<Tile> tilesInArea = getTilesInRange(gameMap, centerTile, areaRadius);
        return tilesInArea.stream()
            .filter(Tile::isOccupied)
            .map(Tile::getOccupant)
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
    }

    public int getAreaRadius() {
        return areaRadius;
    }

    public boolean affectsAllies() {
        return affectsAllies;
    }

    public boolean affectsEnemies() {
        return affectsEnemies;
    }
}
