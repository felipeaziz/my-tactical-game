package com.ibra.tacticalrpg.controller;

import com.ibra.tacticalrpg.action.AttackAction;
import com.ibra.tacticalrpg.action.MoveAction;
import com.ibra.tacticalrpg.entities.Entity;
import com.ibra.tacticalrpg.entities.PlayerEntity;
import com.ibra.tacticalrpg.grid.IsometricGridUtils;
import com.ibra.tacticalrpg.map.HighlightType;
import com.ibra.tacticalrpg.map.isometric.GameMap;
import com.ibra.tacticalrpg.map.isometric.Tile;

import java.util.List;

public class ActionController {

    public void handleActionSelection(int actionIndex, GameMap grid, PlayerEntity player,
                                    EventLogger logger, ItemMenuController itemMenuController,
                                      SkillMenuController skillMenuController) {
        switch (actionIndex) {
            case 0: // Mover
                setupMoveAction(grid, player);
                break;
            case 1: // Atacar
                //TODO - case 1 will be Act and it will open a new menu with options Attack, Skill list, use items and Magic (if any)
                setupAttackAction(grid, player);
                break;
            case 2: // Habilidade
                setupSkillAction(grid, player, logger, skillMenuController);
                break;
            case 3: // Usar Item
                setupItemAction(grid, player, logger, itemMenuController);
                break;
            case 4: // Fim do Turno
                endTurn(grid, player, logger);
                break;
        }
    }

    private void setupMoveAction(GameMap grid, PlayerEntity player) {
        if (!player.hasMoved()) {
            clearHighlights(grid);
            player.getMovableCells(grid).forEach(tile -> {
                tile.setHighlighted(true);
                tile.setHighlightType(HighlightType.MOVE);
            });
            player.setCurrentActionType(PlayerActionType.MOVE);
            player.setCurrentAction(new MoveAction(grid, null, null));
        }
    }

    private void setupAttackAction(GameMap grid, PlayerEntity player) {
        if (!player.hasActed()) {
            clearHighlights(grid);
            player.getAttackableCells(grid).forEach(tile -> {
                tile.setHighlighted(true);
                tile.setHighlightType(HighlightType.ATTACK);
            });
            player.setCurrentActionType(PlayerActionType.ATTACK);
            player.setCurrentAction(new AttackAction(grid, null));
        }
    }

    private void setupSkillAction(GameMap grid, PlayerEntity player, EventLogger logger,
                                  SkillMenuController skillMenuController) {
        if (!player.hasActed()) {
            clearHighlights(grid);
            player.setCurrentActionType(PlayerActionType.SKILL);
            player.setCurrentAction(null);
            skillMenuController.openSkillMenu(player);
            logger.log("Selecione a habilidade.");
        }
    }

    private void setupItemAction(GameMap grid, PlayerEntity player, EventLogger logger,
                               ItemMenuController itemMenuController) {
        if (!player.hasActed()) {
            clearHighlights(grid);
            player.setCurrentActionType(PlayerActionType.ITEM);
            player.setCurrentAction(null);
            itemMenuController.openItemMenu(player);
            logger.log("Selecione um item para usar.");
        }
    }

    private void endTurn(GameMap grid, PlayerEntity player, EventLogger logger) {
        player.setActionDone(true);
        player.setMovedThisTurn(true);
        player.setActedThisTurn(true);
        player.setCurrentAction(null);
        player.setCurrentActionType(PlayerActionType.NONE);
        clearHighlights(grid);
        logger.log("Turno encerrado.");
    }

    public void executeMove(GameMap grid, EventLogger logger, PlayerEntity player, Tile targetTile) {
        if (!player.hasMoved() && player.getMovableCells(grid).contains(targetTile)) {
            Tile fromTile = IsometricGridUtils.findEntityTile(grid, player);
            MoveAction moveAction = new MoveAction(grid, fromTile, targetTile);
            player.setCurrentAction(moveAction);
            moveAction.execute(player, null);
            logger.log("Você se moveu.");
            player.setMovedThisTurn(true);
        }
    }

    public void executeAttack(GameMap grid, List<Entity> entities, EventLogger logger,
                            PlayerEntity player, Tile targetTile) {
        if (!player.hasActed()) {
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
    }

    public void clearHighlights(GameMap grid) {
        grid.getBaseTiles().forEach(tile -> {
            tile.setHighlighted(false);
            tile.setHighlightType(HighlightType.NONE);
        });
    }
}
