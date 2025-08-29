package com.ibra.tacticalrpg.controller;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.ibra.tacticalrpg.entities.Entity;
import com.ibra.tacticalrpg.entities.PlayerEntity;
import com.ibra.tacticalrpg.map.isometric.GameMap;
import com.ibra.tacticalrpg.map.isometric.Tile;
import com.ibra.tacticalrpg.ui.GameUIRenderer;
import com.ibra.tacticalrpg.ui.ItemMenuState;

import java.util.List;

public class PlayerController {
    private final ActionController actionController = new ActionController();
    private final ItemMenuController itemMenuController;

    public PlayerController() {
        this.itemMenuController = new ItemMenuController(actionController);
    }

    public void handleInput(GameMap grid,
                            List<Entity> entities,
                            EventLogger logger,
                            PlayerEntity player,
                            OrthographicCamera camera,
                            GameUIRenderer uiRenderer) {
        if (!canHandleInput(player)) return;

        // Se o menu de itens estiver aberto, trata a entrada dele primeiro
        if (handleItemMenuInput(grid, entities, logger, player, camera, uiRenderer)) {
            return;
        }

        // Verifica se clicou em alguma opção do menu de ações ou em um tile
        if (Gdx.input.justTouched()) {
            handleMouseInput(grid, entities, logger, player, camera, uiRenderer);
        }
    }

    private boolean canHandleInput(PlayerEntity player) {
        if (player.isActionDone() || player.isMoving()) return false;
        if (GameController.getInstance().getCurrentEntity() != player) {
            return false;
        }
        return true;
    }

    private boolean handleItemMenuInput(GameMap grid,
                                      List<Entity> entities,
                                      EventLogger logger,
                                      PlayerEntity player,
                                      OrthographicCamera camera,
                                      GameUIRenderer uiRenderer) {
        if (itemMenuController.getMenuState() == ItemMenuState.CLOSED) {
            return false;
        }

        if(uiRenderer.handleItemMenuClick(itemMenuController, grid, logger, player)) {
            return true;
        }
        itemMenuController.handleItemMenuInput(grid, entities, logger, player, camera);
        return true;
    }

    private void handleMouseInput(GameMap grid,
                                List<Entity> entities,
                                EventLogger logger,
                                PlayerEntity player,
                                OrthographicCamera camera,
                                GameUIRenderer uiRenderer) {
        float mouseX = Gdx.input.getX();
        float mouseY = Gdx.input.getY();

        // Tenta selecionar uma ação do menu
        int actionIndex = uiRenderer.getClickedItemIndex(mouseX, mouseY);
        if (actionIndex >= 0) {
            actionController.handleActionSelection(actionIndex, grid, player, logger, itemMenuController);
            return;
        }

        // Tenta selecionar um tile no mapa
        Tile targetTile = findClickedTile(grid, camera, mouseX, mouseY);
        if (targetTile != null) {
            executeActionOnTile(grid, entities, logger, player, targetTile);
        }
    }

    private Tile findClickedTile(GameMap grid, OrthographicCamera camera, float mouseX, float mouseY) {
        Vector3 screenMouse = new Vector3(mouseX, mouseY, 0);
        Vector3 worldMouse3D = camera.unproject(screenMouse);
        Vector2 worldMouse = new Vector2(worldMouse3D.x, worldMouse3D.y);

        return grid.getBaseTiles().stream()
            .filter(tile -> tile.isHighlighted() && tile.isPointInsideDiamond(worldMouse))
            .findFirst()
            .orElse(null);
    }

    private void executeActionOnTile(GameMap grid,
                                   List<Entity> entities,
                                   EventLogger logger,
                                   PlayerEntity player,
                                   Tile targetTile) {
        switch (player.getCurrentActionType()) {
            case MOVE:
                actionController.executeMove(grid, logger, player, targetTile);
                break;
            case ATTACK:
                actionController.executeAttack(grid, entities, logger, player, targetTile);
                break;
        }

        if (player.hasMoved() && player.hasActed()) {
            player.setActionDone(true);
            player.setCurrentActionType(PlayerActionType.NONE);
        }
        actionController.clearHighlights(grid);
    }

    public ItemMenuController getItemMenuController() {
        return itemMenuController;
    }
}
