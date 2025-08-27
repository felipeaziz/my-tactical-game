package com.ibra.tacticalrpg.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Rectangle;
import com.ibra.tacticalrpg.controller.ItemMenuController;
import com.ibra.tacticalrpg.item.consumable.ConsumableItem;

import java.util.ArrayList;
import java.util.List;

public class ItemMenuRenderer {
    private final BitmapFont font;
    private final ShapeRenderer shapeRenderer;
    private final List<Rectangle> itemBounds = new ArrayList<>(); // Para detectar cliques

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

        itemBounds.clear();
        float padding = 6f;
        float lineHeight = 20f;
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

        String instructions = "Clique no item para selecionar | Esc: Cancelar";
        layout.setText(font, instructions);
        maxWidth = Math.max(maxWidth, layout.width);

        float boxWidth = maxWidth + 2 * padding;
        float boxHeight = lineHeight * (availableItems.size() + 3) + padding * 2; // +3 para título e instruções

//        float boxX = (Gdx.graphics.getWidth() - boxWidth) * 0.5f;
        float boxX = 95f; // Posição fixa do lado do menu base.
//        float boxY = (Gdx.graphics.getHeight() + boxHeight) * 0.5f;
        float boxY = boxHeight + 20f; // Posição fixa do lado do menu Use Item
        // Configurar projeção
        shapeRenderer.setProjectionMatrix(uiMatrix);
        batch.setProjectionMatrix(uiMatrix);

        // Desenhar fundo
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(new Color(0f, 0f, 0f, 0.7f));
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
        textY -= lineHeight;

        // Items
        for (int i = 0; i < availableItems.size(); i++) {
            ConsumableItem item = availableItems.get(i);
            String itemText = (i + 1) + ". " + item.getName();
            Rectangle itemBound = new Rectangle(
                boxX + padding,
                textY - lineHeight,
                boxWidth - 2 * padding,
                lineHeight
            );
            itemBounds.add(itemBound);

            float mouseX = Gdx.input.getX();
            float mouseY = Gdx.input.getY();
            int clickedItemIndex = getClickedItemIndex(mouseX, mouseY);
            if (i == clickedItemIndex) {
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

    /**
     * Verifica se um ponto (coordenadas de tela) está sobre algum item do menu
     * @param screenX coordenada X da tela (do clique)
     * @param screenY coordenada Y da tela (do clique)
     * @return índice do item clicado, ou -1 se não clicou em nenhum item
     */
    public int getClickedItemIndex(float screenX, float screenY) {
        // Converter coordenadas de tela (Y invertido) para coordenadas do UI
        float uiY = Gdx.graphics.getHeight() - screenY;

        for (int i = 0; i < itemBounds.size(); i++) {
            Rectangle bound = itemBounds.get(i);
            if (bound.contains(screenX, uiY)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Limpa os bounds dos itens (chamado quando o menu fecha)
     */
    public void clearItemBounds() {
        itemBounds.clear();
    }
}
