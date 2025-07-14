package com.ibra.tacticalrpg.action;

import com.ibra.tacticalrpg.entities.Entity;
import com.ibra.tacticalrpg.grid.IsometricGridUtils;
import com.ibra.tacticalrpg.map.isometric.GameMap;
import com.ibra.tacticalrpg.map.isometric.Tile;

import java.util.List;

public class MoveAction implements Action {
    private final GameMap gameMap;
    private final Tile fromTile;
    private final Tile toTile;

    public MoveAction(GameMap gameMap, Tile fromTile, Tile toTile) {
        this.gameMap = gameMap;
        this.fromTile = fromTile;
        this.toTile = toTile;
    }

    @Override
    public void execute(Entity actor, Entity target) {
        if (!isValidMove(toTile, actor)) {
            return;
        }

        List<Tile> path = IsometricGridUtils.findPath(gameMap, fromTile, toTile);
        if (!path.isEmpty()) {
            actor.setMovePath(path);
        }
    }

    private boolean isValidMove(Tile to, Entity actor) {
        if (to.isOccupied() && to.getOccupant().isAlive()) {
            System.out.println("Não é possível mover para um tile ocupado por uma entidade viva");
            return false;
        }
        if (to.getTerrainType().isObstacle()) {
            System.out.println("Não é possível mover sobre um obstáculo");
            return false;
        }
        return actor.getMovableCells(gameMap).contains(to);
    }
}
