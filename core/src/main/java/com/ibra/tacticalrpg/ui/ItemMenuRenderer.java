package com.ibra.tacticalrpg.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Matrix4;
import com.ibra.tacticalrpg.controller.ItemMenuController;
import com.ibra.tacticalrpg.item.potion.ConsumableItem;

import java.util.List;

public class ItemMenuRenderer {
    private final BitmapFont font;
    private final ShapeRenderer shapeRenderer;

    public ItemMenuRenderer(BitmapFont font, ShapeRenderer shapeRenderer) {
        this.font = font;
        this.shapeRenderer = shapeRenderer;
    }

    public void renderItemMenu(SpriteBatch batch, ItemMenuController itemController, Matrix4 uiMatrix) {
        if (itemController.getMenuState() != ItemMenuState.SELECTING_ITEM) {
            return;
        }
        List<ConsumableItem> availableItems = itemController.getAvailableItems();
        if (availableItems == null || availableItems.isEmpty()) {
            return;
        }

        float padding = 10f;
        float lineHeight = 25f;
        float itemStartY = 150f; // Altura onde começam os items
        // Medir a largura necessária
        GlyphLayout layout = new GlyphLayout();
        float maxWidth = 0f;
        // Título do menu
        String title = "Selecione um Item:";
        layout.setText(font, title);
        maxWidth = Math.max(maxWidth, layout.width);
        // Items
        for (int i = 0; i < availableItems.size(); i++) {
            ConsumableItem item = availableItems.get(i);
            String itemText = (i + 1) + ". " + item.getName();
            layout.setText(font, itemText);
            maxWidth = Math.max(maxWidth, layout.width);
        }

        String instructions = "↑↓ Navegar | Enter: Selecionar | Esc: Cancelar";
        layout.setText(font, instructions);
        maxWidth = Math.max(maxWidth, layout.width);

        float boxWidth = maxWidth + 2 * padding;
        float boxHeight = lineHeight * (availableItems.size() + 3) + padding * 2; // +3 para título e instruções

        // Posição central da tela
        float boxX = (uiMatrix.val[Matrix4.M00] * 0.5f) - (boxWidth * 0.5f);
        float boxY = itemStartY + boxHeight;

        // Configurar projeção
        shapeRenderer.setProjectionMatrix(uiMatrix);
        batch.setProjectionMatrix(uiMatrix);

        // Desenhar fundo
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(new Color(0.1f, 0.1f, 0.1f, 0.9f));
        shapeRenderer.rect(boxX, boxY - boxHeight, boxWidth, boxHeight);
        shapeRenderer.end();

        // Desenhar borda
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.WHITE);
        shapeRenderer.rect(boxX, boxY - boxHeight, boxWidth, boxHeight);
        shapeRenderer.end();

        // Desenhar conteúdo
        batch.begin();

        float textY = boxY - padding - lineHeight;

        // Título
        font.setColor(Color.YELLOW);
        font.draw(batch, title, boxX + padding, textY);
        textY -= lineHeight * 1.5f;

        // Items
        for (int i = 0; i < availableItems.size(); i++) {
            ConsumableItem item = availableItems.get(i);
            String itemText = (i + 1) + ". " + item.getName();

            // Destacar item selecionado
            if (i == itemController.getSelectedItemIndex()) {
                font.setColor(Color.CYAN);
                // Desenhar background de seleção
                shapeRenderer.setProjectionMatrix(uiMatrix);
                batch.end();
                shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
                shapeRenderer.setColor(new Color(0.3f, 0.3f, 0.6f, 0.5f));
                shapeRenderer.rect(boxX + 2, textY - lineHeight + 5, boxWidth - 4, lineHeight);
                shapeRenderer.end();
                batch.begin();
            } else {
                font.setColor(Color.WHITE);
            }

            font.draw(batch, itemText, boxX + padding, textY);
            textY -= lineHeight;
        }

        // Instruções
        textY -= lineHeight * 0.5f;
        font.setColor(Color.LIGHT_GRAY);
        font.draw(batch, instructions, boxX + padding, textY);

        batch.end();

        // Resetar cor da fonte
        font.setColor(Color.WHITE);
    }
}
