package com.ibra.tacticalrpg.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Matrix4;
import com.ibra.tacticalrpg.entities.PlayerEntity;

public class GameUIRenderer {
    private final BitmapFont font;
    private final ShapeRenderer shapeRenderer;
    private final Matrix4 uiMatrix = new Matrix4();

    public GameUIRenderer(BitmapFont font) {
        this.font = font;
        this.shapeRenderer = new ShapeRenderer();
    }

    public void renderActionMenu(SpriteBatch batch, PlayerEntity player) {
        shapeRenderer.setProjectionMatrix(getUiMatrix());
        batch.setProjectionMatrix(getUiMatrix());

        float menuWidth = 180f;
        float menuHeight = 70f;

        float screenWidth = Gdx.graphics.getWidth();
        float menuX = screenWidth - menuWidth - 20f;
        float menuY = 20f;

        // Fundo do menu
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(new Color(0f, 0f, 0f, 0.7f));
        shapeRenderer.rect(menuX, menuY, menuWidth, menuHeight);
        shapeRenderer.end();

        // Contorno
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.WHITE);
        shapeRenderer.rect(menuX, menuY, menuWidth, menuHeight);
        shapeRenderer.end();

        // Texto
        batch.begin();
        font.setColor(Color.WHITE);
        if (!player.hasMoved()) {
            font.draw(batch, "[1] Mover", menuX + 10, menuY + menuHeight - 10);
        }
        if (!player.hasActed()) {
            font.draw(batch, "[2] Atacar", menuX + 10, menuY + menuHeight - 30);
        }
        font.draw(batch, "[3] Encerrar turno", menuX + 10, menuY + menuHeight - 50);
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
