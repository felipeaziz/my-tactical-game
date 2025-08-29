package com.ibra.tacticalrpg.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Matrix4;
import com.ibra.tacticalrpg.controller.EventLogger;
import com.ibra.tacticalrpg.controller.ItemMenuController;
import com.ibra.tacticalrpg.entities.PlayerEntity;
import com.ibra.tacticalrpg.map.isometric.GameMap;

public class GameUIRenderer extends BaseMenuRenderer {
    private final ItemMenuRenderer itemMenuRenderer;

    public GameUIRenderer(BitmapFont font) {
        super(font, new ShapeRenderer());
        this.itemMenuRenderer = new ItemMenuRenderer(font, shapeRenderer);
    }

    public void renderActionMenu(SpriteBatch batch) {
        clearItemBounds();
        String[] options = {"Mover", "Atacar", "Usar Item", "Fim do Turno"};
        float maxWidth = calculateMaxWidth(options);
        float boxWidth = maxWidth + 2 * PADDING;
        float boxHeight = options.length * LINE_HEIGHT + PADDING * 2;
        float boxX = 20f;
        float boxY = boxHeight + 20f;

        renderMenuBox(batch, boxX, boxY, boxWidth, boxHeight);

        batch.begin();
        float y = boxY - PADDING + 14f;
        for (int i = 0; i < options.length; i++) {
            y -= LINE_HEIGHT;
            addItemBound(
                boxX + PADDING,
                y - LINE_HEIGHT,
                boxWidth - 2 * PADDING,
                LINE_HEIGHT
            );

            boolean isHighlighted = i == getClickedItemIndex(Gdx.input.getX(), Gdx.input.getY());
            renderMenuOption(batch, options[i], boxX, y, boxWidth, isHighlighted);
        }
        batch.end();
    }

    public void renderItemMenu(SpriteBatch batch, ItemMenuController itemController) {
        itemMenuRenderer.renderItemMenu(batch, itemController, getUiMatrix());
    }

    public void renderItemStatus(SpriteBatch batch, ItemMenuController itemController) {
        if (itemController.getMenuState() == ItemMenuState.SELECTING_TARGET) {
            Matrix4 uiMatrix = getUiMatrix();
            batch.setProjectionMatrix(uiMatrix);

            batch.begin();
            font.setColor(Color.YELLOW);
            String message = "Clique no alvo para usar " +
                (itemController.getSelectedItem() != null ?
                    itemController.getSelectedItem().getName() : "item") +
                " | ESC para cancelar";
            font.draw(batch, message, 20, Gdx.graphics.getHeight() - 100);
            font.setColor(Color.WHITE);
            batch.end();
        }
    }

    public boolean handleItemMenuClick(ItemMenuController itemController,
                                       GameMap grid,
                                       EventLogger logger,
                                       PlayerEntity player) {
        if (itemController.getMenuState() != ItemMenuState.SELECTING_ITEM) {
            return false;
        }

        if (Gdx.input.justTouched()) {
            float mouseX = Gdx.input.getX();
            float mouseY = Gdx.input.getY();

            int clickedItemIndex = itemMenuRenderer.getClickedItemIndex(mouseX, mouseY);
            if (clickedItemIndex >= 0) {
                itemController.handleItemClick(clickedItemIndex, grid, logger, player);
                return true;
            }
        }
        return false;
    }

    public Matrix4 getUiMatrix() {
        return uiMatrix.setToOrtho2D(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }

    @Override
    public void dispose() {
        super.dispose();
    }
}
