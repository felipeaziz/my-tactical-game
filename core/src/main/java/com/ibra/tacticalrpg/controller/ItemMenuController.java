package com.ibra.tacticalrpg.controller;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.ibra.tacticalrpg.action.ItemUseAction;
import com.ibra.tacticalrpg.entities.Entity;
import com.ibra.tacticalrpg.entities.PlayerEntity;
import com.ibra.tacticalrpg.item.potion.ConsumableItem;
import com.ibra.tacticalrpg.map.HighlightType;
import com.ibra.tacticalrpg.map.isometric.GameMap;
import com.ibra.tacticalrpg.map.isometric.Tile;
import com.ibra.tacticalrpg.ui.ItemMenuState;

import java.util.List;

import static com.ibra.tacticalrpg.controller.PlayerController.clearHighlights;

public class ItemMenuController {
    private ItemMenuState menuState = ItemMenuState.CLOSED;
    private List<ConsumableItem> availableItems;
    private int selectedItemIndex = 0;
    private ConsumableItem selectedItem;

    /**
     * Lida com input relacionado ao menu de items
     */
    public void handleItemMenuInput(GameMap grid,
                                    List<Entity> entities,
                                    EventLogger logger,
                                    PlayerEntity player,
                                    OrthographicCamera camera) {
        switch (menuState) {
            case SELECTING_ITEM:
                handleItemSelection(grid, logger, player);
                break;
            case SELECTING_TARGET:
                handleTargetSelection(grid, entities, logger, player, camera);
                break;
        }
    }

    /**
     * Abre o menu de seleção de items
     */
    public void openItemMenu(GameMap grid, PlayerEntity player) {
        availableItems = player.getPersonalInventory().getConsumableItems();
        if (availableItems.isEmpty()) {
            System.out.println("Nenhum item consumível disponível!");
            return;
        }
        menuState = ItemMenuState.SELECTING_ITEM;
        selectedItemIndex = 0;
    }

    /**
     * Lida com a seleção do item no menu
     */
    private void handleItemSelection(GameMap grid, EventLogger logger, PlayerEntity player) {
        // Navegação no menu
        if (Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
            selectedItemIndex = Math.max(0, selectedItemIndex - 1);
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) {
            selectedItemIndex = Math.min(availableItems.size() - 1, selectedItemIndex + 1);
        }

        // Confirmação da seleção
        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            selectedItem = availableItems.get(selectedItemIndex);
            menuState = ItemMenuState.SELECTING_TARGET;
            highlightValidTargets(grid, player, selectedItem);
            logger.log("Selecione um alvo para " + selectedItem.getName());
        }
        // Cancelar seleção
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            closeItemMenu(grid);
        }
    }

    /**
     * Lida com a seleção de alvo para o item
     */
    private void handleTargetSelection(GameMap grid,
                                       List<Entity> entities,
                                       EventLogger logger,
                                       PlayerEntity player,
                                       OrthographicCamera camera) {

        // Cancelar seleção de alvo
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            menuState = ItemMenuState.SELECTING_ITEM;
            clearHighlights(grid);
            return;
        }

        // Seleção por clique
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
                executeUseItem(grid, logger, player, selectedItem, targetTile);
            }
        }
    }

    /**
     * Destaca tiles válidos para usar o item
     */
    private void highlightValidTargets(GameMap grid, PlayerEntity player, ConsumableItem item) {
        clearHighlights(grid);
        //TODO - should highlight just at range=1 and self
        for (Tile tile : grid.getBaseTiles()) {
            if (tile.isOccupied()) {
                tile.setHighlighted(true);
                tile.setHighlightType(HighlightType.ITEM); // Você pode precisar adicionar este tipo
            }
        }
    }

    /**
     * Executa o uso do item em um alvo específico
     */
    private void executeUseItem(GameMap grid, EventLogger logger, PlayerEntity player,
                                ConsumableItem item, Tile targetTile) {
        ItemUseAction useAction = new ItemUseAction(item, targetTile, grid);
        player.setCurrentAction(useAction);
        useAction.execute(player, null);

        String targetName = targetTile.isOccupied() ? targetTile.getOccupant().getName() : "área vazia";
        logger.log("Você usou " + item.getName() + " em " + targetName);
        player.setActedThisTurn(true);
        closeItemMenu(grid);
    }

    /**
     * Fecha o menu de items
     */
    public void closeItemMenu(GameMap grid) {
        menuState = ItemMenuState.CLOSED;
        selectedItem = null;
        availableItems = null;
        selectedItemIndex = 0;
        clearHighlights(grid);
    }
    public ItemMenuState getMenuState() {
        return menuState;
    }

    public List<ConsumableItem> getAvailableItems() {
        return availableItems;
    }

    public int getSelectedItemIndex() {
        return selectedItemIndex;
    }

    public ConsumableItem getSelectedItem() {
        return selectedItem;
    }

}
