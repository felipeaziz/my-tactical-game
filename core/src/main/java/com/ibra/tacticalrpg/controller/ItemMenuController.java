package com.ibra.tacticalrpg.controller;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.ibra.tacticalrpg.action.ItemUseAction;
import com.ibra.tacticalrpg.entities.Entity;
import com.ibra.tacticalrpg.entities.PlayerEntity;
import com.ibra.tacticalrpg.item.consumable.ConsumableItem;
import com.ibra.tacticalrpg.map.HighlightType;
import com.ibra.tacticalrpg.map.isometric.GameMap;
import com.ibra.tacticalrpg.map.isometric.Tile;
import com.ibra.tacticalrpg.ui.ItemMenuState;

import java.util.List;
import java.util.stream.Collectors;

public class ItemMenuController implements TileClickHandler {
    private ItemMenuState menuState = ItemMenuState.CLOSED;
    private List<ConsumableItem> availableItems;
    private int selectedItemIndex = 0;
    private ConsumableItem selectedItem;

    /**
     * @param actionController referência mantida para possível uso futuro em funcionalidades de interface
     */
    public ItemMenuController(ActionController actionController) {
        // Constructor mantido para compatibilidade futura
    }

    /**
     * Lida com input relacionado ao menu de items
     */
    public void handleItemMenuInput(GameMap grid, EventLogger logger,
                                  PlayerEntity player, OrthographicCamera camera) {
        switch (menuState) {
            case SELECTING_ITEM:
                handleItemSelection(grid);
                break;
            case SELECTING_TARGET:
                handleTargetSelection(grid, logger, player, camera);
                break;
        }
    }

    /**
     * Abre o menu de seleção de items
     */
    public void openItemMenu(PlayerEntity player) {
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
    private void handleItemSelection(GameMap grid) {
        // Cancelar seleção
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            closeItemMenu(grid);
        }
    }

    public void handleItemClick(int itemIndex, GameMap grid, EventLogger logger, PlayerEntity player) {
        if (menuState != ItemMenuState.SELECTING_ITEM || itemIndex < 0 || itemIndex >= availableItems.size()) {
            return;
        }

        selectedItemIndex = itemIndex;
        selectedItem = availableItems.get(selectedItemIndex);
        menuState = ItemMenuState.SELECTING_TARGET;
        highlightValidTargets(grid, player);
        logger.log("Selecione um alvo para " + selectedItem.getName());
    }

    /**
     * Destaca tiles válidos para usar o item
     */
    private void highlightValidTargets(GameMap grid, PlayerEntity player) {
        grid.clearHighlights();
        List<Tile> targetableTiles = player.getReachableCellsToUseItem(grid).stream()
            .filter(tile -> !tile.getTerrainType().isObstacle())
            .collect(Collectors.toList());

        targetableTiles.forEach(tile -> {
            tile.setHighlighted(true);
            tile.setHighlightType(HighlightType.ITEM);
        });
    }

    /**
     * Lida com a seleção de alvo para o item
     */
    private void handleTargetSelection(GameMap grid, EventLogger logger,
                                       PlayerEntity player, OrthographicCamera camera) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            menuState = ItemMenuState.SELECTING_ITEM;
            grid.clearHighlights();
            return;
        }

        if (Gdx.input.justTouched()) {
            Tile targetTile = findClickedHighlightedTile(grid, camera, Gdx.input.getX(), Gdx.input.getY());
            if (targetTile != null) {
                executeUseItem(grid, logger, player, targetTile);
            }
        }
    }

    /**
     * Executa o uso do item em um alvo específico
     */
    private void executeUseItem(GameMap grid, EventLogger logger, PlayerEntity player, Tile targetTile) {
        ItemUseAction useAction = new ItemUseAction(selectedItem, targetTile, grid);
        player.setCurrentAction(useAction);
        useAction.execute(player, null);

        String targetName = targetTile.isOccupied() ? targetTile.getOccupant().getName() : "área vazia";
        logger.log("Você usou " + selectedItem.getName() + " em " + targetName);
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
        grid.clearHighlights();
    }

    // Getters
    public ItemMenuState getMenuState() { return menuState; }
    public List<ConsumableItem> getAvailableItems() { return availableItems; }
    public ConsumableItem getSelectedItem() { return selectedItem; }
}
