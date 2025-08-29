package com.ibra.tacticalrpg.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Matrix4;
import com.ibra.tacticalrpg.controller.ItemMenuController;
import com.ibra.tacticalrpg.item.consumable.ConsumableItem;

import java.util.List;

public class ItemMenuRenderer extends BaseMenuRenderer {

    public ItemMenuRenderer(BitmapFont font, ShapeRenderer shapeRenderer) {
        super(font, shapeRenderer);
    }

    public void renderItemMenu(SpriteBatch batch, ItemMenuController itemController, Matrix4 uiMatrix) {
        if (itemController.getMenuState() != ItemMenuState.SELECTING_ITEM) {
            return;
        }
        List<ConsumableItem> availableItems = itemController.getAvailableItems();
        if (availableItems == null || availableItems.isEmpty()) {
            return;
        }

        clearItemBounds();
        String title = "Selecione um Item:";
        String[] menuItems = new String[availableItems.size()];
        for (int i = 0; i < availableItems.size(); i++) {
            menuItems[i] = (i + 1) + ". " + availableItems.get(i).getName();
        }
        String instructions = "Clique no item para selecionar | Esc: Cancelar";

        // Calcular largura máxima considerando todos os textos
        float maxWidth = Math.max(
            calculateMaxWidth(new String[]{title, instructions}),
            calculateMaxWidth(menuItems)
        );

        float boxWidth = maxWidth + 2 * PADDING;
        float boxHeight = LINE_HEIGHT * (availableItems.size() + 3) + PADDING * 2; // +3 para título e instruções
        float boxX = 95f;
        float boxY = boxHeight + 20f;

        renderMenuBox(batch, boxX, boxY, boxWidth, boxHeight);

        batch.begin();
        float y = boxY - PADDING - LINE_HEIGHT;

        // Título
        font.setColor(Color.YELLOW);
        font.draw(batch, title, boxX + PADDING, y);
        y -= LINE_HEIGHT;

        // Items
        for (int i = 0; i < menuItems.length; i++) {
            addItemBound(
                boxX + PADDING,
                y - LINE_HEIGHT,
                boxWidth - 2 * PADDING,
                LINE_HEIGHT
            );

            boolean isHighlighted = i == getClickedItemIndex(Gdx.input.getX(), Gdx.input.getY());
            renderMenuOption(batch, menuItems[i], boxX, y, boxWidth, isHighlighted);
            y -= LINE_HEIGHT;
        }

        // Instruções
        y -= LINE_HEIGHT * 0.5f;
        font.setColor(Color.LIGHT_GRAY);
        font.draw(batch, instructions, boxX + PADDING, y);

        batch.end();
        font.setColor(Color.WHITE);
    }
}
