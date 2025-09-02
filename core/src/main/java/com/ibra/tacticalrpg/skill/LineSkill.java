package com.ibra.tacticalrpg.skill;

import com.ibra.tacticalrpg.entities.Entity;
import com.ibra.tacticalrpg.map.isometric.GameMap;
import com.ibra.tacticalrpg.map.isometric.Tile;
import com.ibra.tacticalrpg.skill.effect.SkillEffect;

import java.util.ArrayList;
import java.util.List;

import static com.ibra.tacticalrpg.grid.IsometricGridUtils.findEntityTile;
import static com.ibra.tacticalrpg.grid.IsometricGridUtils.getTilesInLineDirection;

public class LineSkill extends Skill {
    private final int lineLength;
    private final boolean piercing;


    public LineSkill(String name,
                     String description,
                     TargetType targetType,
                     SkillEffect effect,
                     int range,
                     int cooldown,
                     int requiredLevel,
                     int lineLength,
                     boolean piercing) {
        super(name, description, targetType, effect, range, cooldown, requiredLevel);
        this.lineLength = lineLength;
        this.piercing = piercing;
    }

    public void useOnLine(Entity source, Tile targetTile, GameMap gameMap) {
        if (!canUse(source)) {
            System.out.println("Cannot use line skill: " + getName());
            return;
        }
        Tile sourceTile = findEntityTile(gameMap, source);
        List<Tile> tilesInLine = getTilesInLineDirection(gameMap, sourceTile, targetTile, lineLength);
        List<Entity> entitiesHit = new ArrayList<>();
        for (Tile tile : tilesInLine) {
            if (tile.isOccupied() && tile.getOccupant() != source) {
                getEffect().apply(source, tile.getOccupant());
                entitiesHit.add(tile.getOccupant());
                if (!piercing) {
                    break; // Stop if not piercing
                }
            }
        }
        currentCooldown = getCooldown();
        System.out.println(source.getName() + " used line skill: " + getName() +
            " hitting " + entitiesHit.size() + " entities!");
    }

    public int getLineLength() {
        return lineLength;
    }

    public boolean isPiercing() {
        return piercing;
    }
}
