package com.ibra.tacticalrpg.controller;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.ibra.tacticalrpg.action.AttackAction;
import com.ibra.tacticalrpg.action.MoveAction;
import com.ibra.tacticalrpg.entities.Entity;
import com.ibra.tacticalrpg.entities.PlayerEntity;
import com.ibra.tacticalrpg.grid.IsometricGridUtils;
import com.ibra.tacticalrpg.map.HighlightType;
import com.ibra.tacticalrpg.map.isometric.GameMap;
import com.ibra.tacticalrpg.map.isometric.Tile;

import java.util.List;

public class PlayerController {
    public void handleInput(GameMap grid,
                            List<Entity> entities,
                            EventLogger logger,
                            PlayerEntity player,
                            OrthographicCamera camera) {
        if (player.isActionDone() || player.isMoving()) return;
        if (GameController.getInstance().getCurrentEntity() != player) {
            return;
        }

        // Input para seleção de ação
        if (!player.hasMoved() && Gdx.input.isKeyJustPressed(Input.Keys.NUM_1)) {
            clearHighlights(grid);
            for (Tile tile : player.getMovableCells(grid)) {
                tile.setHighlighted(true);
                tile.setHighlightType(HighlightType.MOVE);
            }
            player.setCurrentActionType(PlayerActionType.MOVE);
            player.setCurrentAction(new MoveAction(grid, null, null));
        } else if (!player.hasActed() && Gdx.input.isKeyJustPressed(Input.Keys.NUM_2)) {
            clearHighlights(grid);
            for (Tile tile : player.getAttackableCells(grid)) {
                tile.setHighlighted(true);
                tile.setHighlightType(HighlightType.ATTACK);
            }
            player.setCurrentActionType(PlayerActionType.ATTACK);
            player.setCurrentAction(new AttackAction(grid, null));
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_3)) {
            player.setActionDone(true);
            player.setMovedThisTurn(true);
            player.setActedThisTurn(true);
            player.setCurrentAction(null);
            player.setCurrentActionType(PlayerActionType.NONE);
            clearHighlights(grid);
            logger.log("Turno encerrado.");
        }

        // Input para seleção de tile
        if (Gdx.input.justTouched()) {
            Vector3 screenMouse = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
            Vector3 worldMouse3D = camera.unproject(screenMouse);
            Vector2 worldMouse = new Vector2(worldMouse3D.x, worldMouse3D.y);
            Tile targetTile = null;
            for (Tile tile : grid.getBaseTiles()) {
                if (tile.isHighlighted() && tile.isPointInsideDiamond(worldMouse)) {
                    targetTile = tile;
                    break;
                }
            }
            if (targetTile != null) {
                switch (player.getCurrentActionType()) {
                    case MOVE:
                        if (!player.hasMoved() && player.getMovableCells(grid).contains(targetTile)) {
                            executeMove(grid, logger, player, targetTile);
                        }
                        break;
                    case ATTACK:
                        if (!player.hasActed()) {
                            executeAttack(grid, entities, logger, player, targetTile);
                        }
                        break;
                }

                if (player.hasMoved() && player.hasActed()) {
                    player.setActionDone(true);
                    player.setCurrentActionType(PlayerActionType.NONE);
                }
                clearHighlights(grid);
            }
        }
    }

    private static void executeAttack(GameMap grid, List<Entity> entities, EventLogger logger, PlayerEntity player, Tile targetTile) {
        if (!targetTile.isOccupied()) {
            logger.log("Ataque falhou!");
        } else {
            Entity target = targetTile.getOccupant();
            if (entities.contains(target)) {
                AttackAction attackAction = new AttackAction(grid, targetTile);
                player.setCurrentAction(attackAction);
                attackAction.execute(player, target);
                if (!target.isAlive()) {
                    logger.log("Inimigo derrotado!");
                } else {
                    logger.log("Ataque bem-sucedido! HP restante: " + target.getStats().getCurrentHp());
                }
            }
        }
        player.setActedThisTurn(true);
    }

    private static void executeMove(GameMap grid, EventLogger logger, PlayerEntity player, Tile targetTile) {
        Tile fromTile = IsometricGridUtils.findEntityTile(grid, player);
        MoveAction moveAction = new MoveAction(grid, fromTile, targetTile);
        player.setCurrentAction(moveAction);
        moveAction.execute(player, null);
        logger.log("Você se moveu.");
    }

    private static void clearHighlights(GameMap grid) {
        grid.getBaseTiles().forEach(tile -> {
            tile.setHighlighted(false);
            tile.setHighlightType(HighlightType.NONE);
        });
    }
}
