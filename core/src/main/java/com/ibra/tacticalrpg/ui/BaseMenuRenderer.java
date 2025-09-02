package com.ibra.tacticalrpg.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Rectangle;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseMenuRenderer {
    protected final BitmapFont font;
    protected final ShapeRenderer shapeRenderer;
    protected final List<Rectangle> itemBounds = new ArrayList<>();
    protected final Matrix4 uiMatrix = new Matrix4();
    protected static final float PADDING = 6f;
    protected static final float LINE_HEIGHT = 20f;
    protected static final Color BACKGROUND_COLOR = new Color(0f, 0f, 0f, 0.7f);
    protected static final Color HIGHLIGHT_COLOR = new Color(0.3f, 0.3f, 0.6f, 0.5f);

    public BaseMenuRenderer(BitmapFont font, ShapeRenderer shapeRenderer) {
        this.font = font;
        this.shapeRenderer = shapeRenderer;
    }

    protected void renderMenuBox(SpriteBatch batch, float boxX, float boxY, float boxWidth, float boxHeight) {
        Matrix4 matrix = getUiMatrix();
        shapeRenderer.setProjectionMatrix(matrix);
        batch.setProjectionMatrix(matrix);

        // Desenhar fundo
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(BACKGROUND_COLOR);
        shapeRenderer.rect(boxX, boxY - boxHeight, boxWidth, boxHeight);
        shapeRenderer.end();

        // Desenhar borda
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.WHITE);
        shapeRenderer.rect(boxX, boxY - boxHeight, boxWidth, boxHeight);
        shapeRenderer.end();
    }

    protected void renderMenuOption(SpriteBatch batch, String text, float boxX, float boxY,
                                    float boxWidth, boolean isHighlighted) {
        if (isHighlighted) {
            font.setColor(Color.CYAN);
            batch.end();
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            shapeRenderer.setColor(HIGHLIGHT_COLOR);
            shapeRenderer.rect(boxX + 2, boxY - LINE_HEIGHT + 5, boxWidth - 4, LINE_HEIGHT);
            shapeRenderer.end();
            batch.begin();
        } else {
            font.setColor(Color.WHITE);
        }
        font.draw(batch, text, boxX + PADDING, boxY);
    }

    protected float calculateMaxWidth(String[] texts) {
        GlyphLayout layout = new GlyphLayout();
        float maxWidth = 0f;
        for (String text : texts) {
            layout.setText(font, text);
            if (layout.width > maxWidth) {
                maxWidth = layout.width;
            }
        }
        return maxWidth;
    }

    public int getClickedIndex(float screenX, float screenY) {
        float uiY = Gdx.graphics.getHeight() - screenY;
        for (int i = 0; i < itemBounds.size(); i++) {
            Rectangle bound = itemBounds.get(i);
            if (bound.contains(screenX, uiY)) {
                return i;
            }
        }
        return -1;
    }

    protected void clearItemBounds() {
        itemBounds.clear();
    }

    protected void addItemBound(float x, float y, float width, float height) {
        itemBounds.add(new Rectangle(x, y, width, height));
    }

    public void dispose() {
        shapeRenderer.dispose();
    }

    protected Matrix4 getUiMatrix() {
        return uiMatrix.setToOrtho2D(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }
}
