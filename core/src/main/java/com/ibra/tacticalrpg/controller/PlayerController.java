package com.ibra.tacticalrpg.controller;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.ibra.tacticalrpg.BattleScreen;
import com.ibra.tacticalrpg.action.AttackAction;
import com.ibra.tacticalrpg.action.MoveAction;
import com.ibra.tacticalrpg.entities.Entity;
import com.ibra.tacticalrpg.entities.PlayerEntity;
import com.ibra.tacticalrpg.grid.GridUtils;
import com.ibra.tacticalrpg.map.orthogonal.GameMap;
import com.ibra.tacticalrpg.map.orthogonal.Tile;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PlayerController {
    private Set<Tile> highlightedCells = new HashSet<>();

    public void handleInput(GameMap grid, List<Entity> entities, EventLogger logger, PlayerEntity player) {
        if (player.isActionDone()) return;

        // Input para seleção de ação
        if (!player.hasMoved() && Gdx.input.isKeyJustPressed(Input.Keys.NUM_1)) {
            highlightedCells = player.getMovableCells(grid);
            player.setCurrentAction(new MoveAction(grid, null, null));
        } else if (!player.hasActed() && Gdx.input.isKeyJustPressed(Input.Keys.NUM_2)) {
            highlightedCells = player.getAttackableCells(grid);
            player.setCurrentAction(new AttackAction(grid, null));
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_3)) {
            player.setActionDone(true);
            player.setCurrentAction(null);
            highlightedCells.clear();
            logger.log("Turno encerrado.");
        }

        // Input para seleção de tile
        if (Gdx.input.justTouched()) {
            int mouseX = Gdx.input.getX();
            int mouseY = Gdx.input.getY();
            int windowHeight = Gdx.graphics.getHeight();
            int cellX = (mouseX - BattleScreen.GRID_ORIGIN_X) / BattleScreen.CELL_SIZE;
            int cellY = ((windowHeight - mouseY) - BattleScreen.GRID_ORIGIN_Y) / BattleScreen.CELL_SIZE;
            cellY = grid.getHeight() - 1 - cellY;

            Tile targetTile = grid.getTile(cellX, cellY);
            if (targetTile != null && highlightedCells.contains(targetTile)) {
                if (player.getCurrentAction() instanceof MoveAction
                    && !player.hasMoved()
                    && player.getMovableCells(grid).contains(targetTile)) {
                    // Movimento
                    Tile fromTile = GridUtils.findEntityTile(grid, player);
                    MoveAction moveAction = new MoveAction(grid, fromTile, targetTile);
                    player.setCurrentAction(moveAction);
                    moveAction.execute(player, null);
                    player.setMovedThisTurn(true);
                    logger.log("Você se moveu.");
                } else if (player.getCurrentAction() instanceof AttackAction
                    && !player.hasActed()) {
                    if(!targetTile.isOccupied()) {
                        logger.log("Ataque falhou!");
                    } else {
                        // Ataque
                        Entity target = targetTile.getOccupant();
                        if (entities.contains(target)) {
                            AttackAction attackAction = new AttackAction(grid, targetTile);
                            player.setCurrentAction(attackAction);
                            attackAction.execute(player, target);
                            if (!target.isAlive()) {
                                logger.log("Inimigo derrotado!");
                            } else {
                                logger.log("Ataque bem-sucedido! HP restante: " + target.getStats().getCurrentHp() + " HP");
                            }
                        }
                    }
                    player.setActedThisTurn(true);
                }

                if (player.hasMoved() && player.hasActed()) {
                    player.setActionDone(true);
                }
                highlightedCells.clear();
            }
        }
    }

    public Set<Tile> getHighlightedCells() {
        return highlightedCells;
    }
}
