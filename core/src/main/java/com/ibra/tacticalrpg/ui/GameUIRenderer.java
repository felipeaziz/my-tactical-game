package com.ibra.tacticalrpg.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.ibra.tacticalrpg.entities.PlayerEntity;

public class GameUIRenderer {
    private final BitmapFont font;
    private final ShapeRenderer shapeRenderer;

    public GameUIRenderer(BitmapFont font) {
        this.font = font;
        this.shapeRenderer = new ShapeRenderer();
    }

    public void renderActionMenu(SpriteBatch batch,
                                 OrthographicCamera camera,
                                 PlayerEntity player) {
        // Calcular posição do menu com base na câmera
        float menuX = camera.position.x - camera.viewportWidth / 2 + 20;
        float menuY = camera.position.y - camera.viewportHeight / 2 + 20;
        float menuWidth = 180f;
        float menuHeight = 70f;

        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(new Color(0f, 0f, 0f, 0.7f));
        shapeRenderer.rect(menuX, menuY, menuWidth, menuHeight);
        shapeRenderer.end();
        // Renderizar contorno
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.WHITE);
        shapeRenderer.rect(menuX, menuY, menuWidth, menuHeight);
        shapeRenderer.end();

        batch.begin();
        font.setColor(Color.WHITE);
        if (!player.hasMoved()) {
            font.draw(batch, "[1] Mover", menuX + 10, menuY + menuHeight -10);
        }
        if (!player.hasActed()) {
            font.draw(batch, "[2] Atacar", menuX + 10, menuY + menuHeight - 30);
        }
        font.draw(batch, "[3] Encerrar turno", menuX + 10, menuY + menuHeight - 50);
        batch.end();
    }

    public void dispose() {
        shapeRenderer.dispose();
    }
}
