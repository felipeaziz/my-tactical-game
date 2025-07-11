package com.ibra.tacticalrpg.grid;

import com.ibra.tacticalrpg.entities.Entity;
import com.ibra.tacticalrpg.map.isometric.GameMap;
import com.ibra.tacticalrpg.map.isometric.Tile;

import java.util.Comparator;
import java.util.List;

public class IsometricGridUtils {
    public static List<Tile> findPath(GameMap grid, Tile start, Tile end) {
        return null; //TODO
    }

    public static GameMap getGrid(Entity entity) {
        return null; //TODO
    }

    public static Tile findEntityTile(GameMap map, Entity entity) {
        return map.getBaseTiles().stream()
            .filter(tile -> entity.equals(tile.getOccupant()))
            .findAny().orElse(null);
    }

    public static Comparator<Entity> byWorldY(GameMap map) {
        return Comparator.comparing(entity -> {
            Tile tile = findEntityTile(map, entity);
            return tile != null ? tile.getWorldPosition().y : 0;
        });
    }
}
