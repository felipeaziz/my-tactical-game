package com.ibra.tacticalrpg.map.isometric;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.ibra.tacticalrpg.map.TerrainType;

import java.util.LinkedList;

public class GameMap {
    final float TILE_WIDTH = 64f;
    final float TILE_HEIGHT = 32f;
    private final ShapeRenderer shapeRenderer;

    LinkedList<Tile> base;
    LinkedList<Tile> objects;
    LinkedList<Tile> visualEffects;

    public GameMap() {
        base = new LinkedList<>();
        objects = new LinkedList<>();
        visualEffects = new LinkedList<>();
        shapeRenderer = new ShapeRenderer();
        fillMap(); // Fill the map with tiles
    }

    public void fillMap() {
        // Fill the map with tiles or entities
        for (int row = 9; row >= 0; row--) {
            for (int col = 9; col >= 0; col--) {
                float x = (col - row) * (TILE_WIDTH / 2f); // Adjusted for isometric projection
                float y = (col + row) * (TILE_HEIGHT / 2f); // Adjusted for isometric projection
                Tile tile = new Tile(new Vector2(row, col), new Vector2(x, y), TerrainType.NORMAL);
                base.add(tile);
                visualEffects.add(tile); // Add to visual effects for hover detection

                // Add some objects for demonstration
                if (row == 5 && col == 5) {
                    Tile objectTile = new Tile(new Vector2(row, col), new Vector2(x, y), TerrainType.SNOW);
                    base.add(objectTile);
                } else if (row == 3 && col == 3) {
                    Tile objectTile = new Tile(new Vector2(row, col), new Vector2(x, y), TerrainType.GRASS);
                    base.add(objectTile);
                }
            }
        }
    }

    public void render(SpriteBatch batch, OrthographicCamera camera) {
        renderBaseLayer(batch, camera);
        renderVisualEffectLayer(batch, camera);
        renderObjectLayer(batch);
    }

    private void renderBaseLayer(SpriteBatch batch, OrthographicCamera camera) {
        batch.begin();
        for (Tile tile : base) {
            tile.renderTexture(batch);
        }
        batch.end();
        shapeRenderer.setProjectionMatrix(batch.getProjectionMatrix());
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.WHITE);
        for (Tile tile : base) {
            tile.renderOutline(shapeRenderer);
        }
        shapeRenderer.end();
    }

    private void renderVisualEffectLayer(SpriteBatch batch, OrthographicCamera camera) {
        shapeRenderer.setProjectionMatrix(batch.getProjectionMatrix());
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.WHITE);
        for (Tile tile : visualEffects) {
            tile.renderVisualEffect(shapeRenderer, camera);
        }
        shapeRenderer.end();
    }

    private void renderObjectLayer(SpriteBatch batch) {
        batch.begin();
        for (Tile tile : objects) {
            tile.renderTexture(batch);
        }
        batch.end();
    }
}
