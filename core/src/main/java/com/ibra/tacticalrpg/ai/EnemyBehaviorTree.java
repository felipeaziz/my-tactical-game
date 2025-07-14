package com.ibra.tacticalrpg.ai;

import com.ibra.tacticalrpg.action.AttackAction;
import com.ibra.tacticalrpg.action.MoveAction;
import com.ibra.tacticalrpg.entities.EnemyEntity;
import com.ibra.tacticalrpg.entities.Entity;
import com.ibra.tacticalrpg.entities.PlayerEntity;
import com.ibra.tacticalrpg.grid.IsometricGridUtils;
import com.ibra.tacticalrpg.map.isometric.GameMap;
import com.ibra.tacticalrpg.map.isometric.Tile;

import java.util.List;
import java.util.Set;

public class EnemyBehaviorTree {
    public static Task<EnemyEntity> createBehaviorTree() {
        Sequence<EnemyEntity> sequence = new Sequence<>();
        sequence.addChild(new FirstAttackTask());
        sequence.addChild(new MoveIfNeededTask());
        sequence.addChild(new SecondAttackTask());
        return sequence;
    }

    // Tenta atacar se possível
    public static class FirstAttackTask extends Task<EnemyEntity> {
        @Override
        public void run() {
            EnemyEntity enemy = getObject();
            if (!enemy.hasActed()) {
                Entity target = findNearestPlayer(enemy);
                if (target != null && canAttack(enemy, target)) {
                    GameMap grid = IsometricGridUtils.getGrid(enemy);
                    Tile targetTile = IsometricGridUtils.findEntityTile(grid, target);
                    AttackAction attack = new AttackAction(grid, targetTile);
                    attack.execute(enemy, target);
                    enemy.setActedThisTurn(true);
                }
            }
            success();
        }

        private boolean canAttack(Entity attacker, Entity target) {
            GameMap grid = IsometricGridUtils.getGrid(attacker);
            Set<Tile> attackableCells = attacker.getAttackableCells(grid);
            Tile targetTile = IsometricGridUtils.findEntityTile(grid, target);
            return targetTile != null && attackableCells.contains(targetTile);
        }
    }

    // Move em direção ao alvo se necessário
    public static class MoveIfNeededTask extends Task<EnemyEntity> {
        @Override
        public void run() {
            EnemyEntity enemy = getObject();
            if (!enemy.hasMoved()) {
                Entity target = findNearestPlayer(enemy);
                if (target != null) {
                    moveTowardsTarget(enemy, target);
                }
            }
            success();
        }

        private void moveTowardsTarget(EnemyEntity enemy, Entity target) {
            GameMap grid = IsometricGridUtils.getGrid(enemy);
            Tile targetTile = IsometricGridUtils.findEntityTile(grid, target);
            if (targetTile == null) return;

            Set<Tile> movableCells = enemy.getMovableCells(grid);
            Tile bestMove = findBestMove(movableCells, targetTile);
            if (bestMove != null) {
                Tile fromTile = IsometricGridUtils.findEntityTile(grid, enemy);
                if (fromTile != null) {
                    MoveAction moveAction = new MoveAction(grid, fromTile, bestMove);
                    moveAction.execute(enemy, null);
                }
            }
            enemy.setMovedThisTurn(true);  // Marca que o inimigo se moveu
        }

        private Tile findBestMove(Set<Tile> movableCells, Tile targetTile) {
            Tile bestMove = null;
            int minDistance = Integer.MAX_VALUE;
            for (Tile cell : movableCells) {
                if (cell.getTerrainType().isObstacle() ||
                    (cell.isOccupied() && cell.getOccupant().isAlive())) continue;
                int distance = Math.abs(cell.getGridPositionX() - targetTile.getGridPositionX())
                    + Math.abs(cell.getGridPositionY() - targetTile.getGridPositionY());
                if (distance < minDistance) {
                    minDistance = distance;
                    bestMove = cell;
                }
            }
            return bestMove;
        }
    }

    // Tenta atacar novamente após mover
    public static class SecondAttackTask extends Task<EnemyEntity> {
        @Override
        public void run() {
            EnemyEntity enemy = getObject();
            if (!enemy.hasActed()) {
                Entity target = findNearestPlayer(enemy);
                if (target != null && canAttack(enemy, target)) {
                    GameMap grid = IsometricGridUtils.getGrid(enemy);
                    Tile targetTile = IsometricGridUtils.findEntityTile(grid, target);
                    AttackAction attack = new AttackAction(grid, targetTile);
                    attack.execute(enemy, target);
                }
                enemy.setActedThisTurn(true);
            }
            success();
        }

        private boolean canAttack(Entity attacker, Entity target) {
            GameMap grid = IsometricGridUtils.getGrid(attacker);
            Set<Tile> attackableCells = attacker.getAttackableCells(grid);
            Tile targetTile = IsometricGridUtils.findEntityTile(grid, target);
            return targetTile != null && attackableCells.contains(targetTile);
        }
    }

    private static Entity findNearestPlayer(Entity enemy) {
        GameMap grid = IsometricGridUtils.getGrid(enemy);
        if (grid == null) return null;

        List<Entity> entities = grid.getEntities();
        Entity nearest = null;
        int minDistance = Integer.MAX_VALUE;

        Tile enemyTile = IsometricGridUtils.findEntityTile(grid, enemy);
        if (enemyTile == null) return null;

        for (Entity entity : entities) {
            if (entity instanceof PlayerEntity && entity.isAlive()) {
                Tile targetTile = IsometricGridUtils.findEntityTile(grid, entity);
                if (targetTile != null) {
                    int distance = Math.abs(enemyTile.getGridPositionX() - targetTile.getGridPositionX())
                        + Math.abs(enemyTile.getGridPositionY() - targetTile.getGridPositionY());
                    if (distance < minDistance) {
                        minDistance = distance;
                        nearest = entity;
                    }
                }
            }
        }
        return nearest;
    }
}
