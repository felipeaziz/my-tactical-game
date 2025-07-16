package com.ibra.tacticalrpg.map.isometric;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.ibra.tacticalrpg.entities.EnemyEntity;
import com.ibra.tacticalrpg.entities.Entity;
import com.ibra.tacticalrpg.entities.PlayerEntity;
import com.ibra.tacticalrpg.job.Apprentice;
import com.ibra.tacticalrpg.map.TerrainType;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import static com.ibra.tacticalrpg.grid.IsometricGridUtils.byWorldY;
import static com.ibra.tacticalrpg.grid.IsometricGridUtils.findEntityTile;

public class GameMap {
    final float TILE_WIDTH = 64f;
    final float TILE_HEIGHT = 32f;
    private final ShapeRenderer shapeRenderer;

    LinkedList<Tile> base;
    LinkedList<Tile> objects;
    LinkedList<Tile> visualEffects;
    LinkedList<Entity> entities;

    public GameMap() {
        base = new LinkedList<>();
        objects = new LinkedList<>();
        visualEffects = new LinkedList<>();
        entities = new LinkedList<>();
        shapeRenderer = new ShapeRenderer();
        fillMap(); // Fill the map with tiles
        addEntities();
    }

    public void render(SpriteBatch batch, OrthographicCamera camera) {
        renderBaseLayer(batch);
        renderVisualEffectLayer(batch, camera);
        renderObjectLayer(batch);
        renderEntities(batch);
    }

    public void fillMap() {
        // Fill the map with tiles or entities
        for (int row = 9; row >= 0; row--) {
            for (int col = 9; col >= 0; col--) {
                float x = calculateWorldPositionX(col, row); // Adjusted for isometric projection
                float y = calculateWorldPositionY(col, row); // Adjusted for isometric projection
                TerrainType terrainType = TerrainType.NORMAL; // Default terrain type
                if (row == 3) {
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
                Tile tile = new Tile(new Vector2(col, row), new Vector2(x, y), terrainType);
                base.add(tile);
                visualEffects.add(tile); // Add to visual effects for hover detection

                //Add some rocks and trees on object layer for testing
                if (row == 0) {
                    Tile objectTile = new Tile(new Vector2(col, row), new Vector2(x, y), TerrainType.TREE);
                    objects.add(objectTile);
                } else if ((row == 2 && col == 1) || (row == 8 && col == 9)) {
                    Tile objectTile = new Tile(new Vector2(col, row), new Vector2(x, y), TerrainType.ROCK);
                    objects.add(objectTile);
                }
            }
        }
    }

    private void addEntities() {
        Tile tile1 = getTile(2, 2);
        Entity hero = new PlayerEntity("Hero", new Apprentice());
        tile1.setOccupant(hero);
        entities.add(hero);

        Tile tile2 = getTile(9, 9);
        Entity enemy = new EnemyEntity("Enemy", new Apprentice());
        tile2.setOccupant(enemy);
        entities.add(enemy);
    }

    private float calculateWorldPositionY(int col, int row) {
        return (col + row) * (TILE_HEIGHT / 2f);
    }

    private float calculateWorldPositionX(int col, int row) {
        return (col - row) * (TILE_WIDTH / 2f);
    }

    private void renderBaseLayer(SpriteBatch batch) {
        batch.begin();
        for (Tile tile : base) {
            tile.renderTexture(batch);
        }
        batch.end();
        shapeRenderer.setProjectionMatrix(batch.getProjectionMatrix());
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        base.forEach(tile -> tile.renderOutline(shapeRenderer));
        shapeRenderer.end();
    }

    private void renderVisualEffectLayer(SpriteBatch batch, OrthographicCamera camera) {
        shapeRenderer.setProjectionMatrix(batch.getProjectionMatrix());
        //Preencher tiles destacados
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        visualEffects.stream().filter(Tile::isHighlighted).forEach(tile -> tile.renderHighlightFill(shapeRenderer));
        shapeRenderer.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);
        // Redesenhar contornos de tiles destacados
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        visualEffects.stream().filter(Tile::isHighlighted).forEach(tile -> tile.renderOutline(shapeRenderer));
        //Contornar se mouse está sobre
        visualEffects.forEach(tile -> tile.renderOutlineIfHovered(shapeRenderer, camera));
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

    private void renderEntities(SpriteBatch batch) {
        entities.sort(byWorldY(this));
        batch.begin();
        for (Entity entity : entities) {
            Tile tile = findEntityTile(this, entity);
            if (tile != null) {
                entity.render(batch, tile.getWorldPosition());
            }
        }
        batch.end();

        shapeRenderer.setProjectionMatrix(batch.getProjectionMatrix());
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        for (Entity entity : entities) {
            Tile tile = findEntityTile(this, entity);
            if (tile != null) {
                entity.renderStatusBars(shapeRenderer, tile.getWorldPosition());
            }
        }
        shapeRenderer.end();

    }

    public Tile getTile(int x, int y) {
        return base.stream()
            .filter(tile -> tile.getGridPositionX() == x && tile.getGridPositionY() == y)
            .findFirst()
            .orElse(null);
    }

    public boolean isTileBlocked(int x, int y) {
        Tile objectTile = objects.stream()
            .filter(tile -> tile.getGridPositionX() == x && tile.getGridPositionY() == y)
            .findFirst()
            .orElse(null);
        return objectTile != null && objectTile.getTerrainType().isObstacle();
    }

    public LinkedList<Tile> getBaseTiles() {
        return base;
    }

    public List<Entity> getEntities() {
        return entities;
    }
}
