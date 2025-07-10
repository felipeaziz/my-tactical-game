package com.ibra.tacticalrpg.map.isometric;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.ibra.tacticalrpg.map.TerrainType;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

public class GameMap {
    final float TILE_WIDTH = 64f;
    final float TILE_HEIGHT = 32f;
    private final ShapeRenderer shapeRenderer;

    private Tile selectedTile;
    private List<Tile> highlightedTiles;

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
                // Create a tile with a specific terrain type based on its position
                TerrainType terrainType = TerrainType.NORMAL; // Default terrain type
                if(row == 3) {
                    terrainType = TerrainType.SNOW; // Example
                } else if (row == 5 || row == 7) {
                    terrainType = TerrainType.GRASS; // Example
                } else if (row == 6) {
                    terrainType = TerrainType.WATER; // Example
                } else if (row == 9) {
                    terrainType = TerrainType.SAND; // Example
                } else if (row == 4) {
                    terrainType = TerrainType.SWAMP; // Example
                }
                Tile tile = new Tile(new Vector2(row, col), new Vector2(x, y), terrainType);
                base.add(tile);
                visualEffects.add(tile); // Add to visual effects for hover detection

                //Add some rocks and trees on object layer for testing
                if(row == 0) {
                    Tile objectTile = new Tile(new Vector2(row, col), new Vector2(x, y), TerrainType.TREE);
                    objects.add(objectTile);
                } else if ((row == 1 && col == 1) || (row == 8 && col == 8)) {
                    Tile objectTile = new Tile(new Vector2(row, col), new Vector2(x, y), TerrainType.ROCK);
                    objects.add(objectTile);
                }
            }
        }
    }

    public void render(SpriteBatch batch, OrthographicCamera camera) {
        renderBaseLayer(batch);
        renderVisualEffectLayer(batch, camera);
        renderObjectLayer(batch);
    }

    private void renderBaseLayer(SpriteBatch batch) {
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
        objects.sort(Comparator.comparing(tile -> tile.getWorldPosition().y));
        batch.begin();
        for (Tile tile : objects) {
            tile.renderTexture(batch);
        }
        batch.end();
    }

    public Tile getTileAt(Vector2 worldPosition) {
        for (Tile tile : base) {
            if (tile.getWorldPosition().equals(worldPosition)) {
                return tile;
            }
        }
        return null;
    }

    public void selectTile(Tile tile) {
        selectedTile = tile;
    }
}
