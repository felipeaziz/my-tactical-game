package com.ibra.tacticalrpg.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Matrix4;
import com.ibra.tacticalrpg.entities.PlayerEntity;
import com.ibra.tacticalrpg.map.isometric.GameMap;

public class GameUIRenderer {
    private final BitmapFont font;
    private final ShapeRenderer shapeRenderer;
    private final Matrix4 uiMatrix = new Matrix4();

    public GameUIRenderer(BitmapFont font) {
        this.font = font;
        this.shapeRenderer = new ShapeRenderer();
    }

    public void renderActionMenu(SpriteBatch batch, GameMap map, PlayerEntity player) {
        float padding = 6f;
        float lineHeight = 20;
        String[] options = {"1 - Mover", "2 - Atacar", "3 - Fim do Turno"};

        // Medir o maior texto para definir largura da caixa
        GlyphLayout layout = new GlyphLayout();
        float maxWidth = 0f;
        for (String option : options) {
            layout.setText(font, option);
            if (layout.width > maxWidth) {
                maxWidth = layout.width;
            }
        }

        float boxWidth = maxWidth + 2 * padding;
        float boxHeight = options.length * lineHeight + padding * 2;

        // Posição fixa no canto inferior esquerdo
        float boxX = 20f;
        float boxY = boxHeight + 20f;
        Matrix4 uiMatrix = getUiMatrix();
        shapeRenderer.setProjectionMatrix(uiMatrix);
        batch.setProjectionMatrix(uiMatrix);
        // Desenhar fundo
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(new Color(0f, 0f, 0f, 0.7f)); // fundo escuro
        shapeRenderer.rect(boxX, boxY - boxHeight, boxWidth, boxHeight);
        shapeRenderer.end();
        // Desenhar borda
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.WHITE); // borda branca
        shapeRenderer.rect(boxX, boxY - boxHeight, boxWidth, boxHeight);
        shapeRenderer.end();
        // Desenhar as opções
        batch.begin();
        float verticalOffset = 14f;
        float y = boxY - padding + verticalOffset;
        for (String option : options) {
            y -= lineHeight;
            font.draw(batch, option, boxX + padding, y);
        }
        batch.end();
    }

    public Matrix4 getUiMatrix() {
        // Reset para identidade (sem projeção), útil para coordenadas de tela
        return uiMatrix.setToOrtho2D(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }

    public void dispose() {
        shapeRenderer.dispose();
    }
}
