package com.ibra.tacticalrpg.map.isometric;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.ibra.tacticalrpg.entities.EnemyEntity;
import com.ibra.tacticalrpg.entities.Entity;
import com.ibra.tacticalrpg.entities.PlayerEntity;
import com.ibra.tacticalrpg.item.consumable.Antidote;
import com.ibra.tacticalrpg.item.consumable.HealingPotion;
import com.ibra.tacticalrpg.job.Apprentice;
import com.ibra.tacticalrpg.job.Carrier;
import com.ibra.tacticalrpg.map.TerrainType;
import com.ibra.tacticalrpg.skill.LineSkill;
import com.ibra.tacticalrpg.skill.TargetType;
import com.ibra.tacticalrpg.skill.effect.DamageEffect;
import com.ibra.tacticalrpg.ui.RenderItem;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import static com.ibra.tacticalrpg.grid.IsometricGridUtils.*;

public class GameMap {
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

        renderObjectsAndEntities(batch);

        renderStatusBars(batch);
    }

    private void renderObjectsAndEntities(SpriteBatch batch) {
        List<RenderItem> renderQueue = new ArrayList<>();
        objects.forEach(o -> renderQueue.add(new RenderItem(o.getWorldPosition().y,
            () -> o.renderTexture(batch))));
        entities.forEach(e -> {
            Tile tile = findEntityTile(this, e);
            if (tile != null) {
                renderQueue.add(new RenderItem(tile.getWorldPosition().y,
                    () -> e.render(batch, tile.getWorldPosition())));
            }
        });
        renderQueue.sort(Comparator.comparingDouble(item -> -item.drawOrderY));
        batch.begin();
        for (RenderItem item : renderQueue) {
            item.render.run();
        }
        batch.end();
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
        hero.getJob().applyInitialSkills(hero.getStats());
        hero.getJob().addSkill(new LineSkill("Earth Slash", "Damage all entities in a line", TargetType.LINE,
            new DamageEffect(4), 5, 1, 1, 5, true));
        hero.getPersonalInventory().addItem(new HealingPotion(), 1);
        tile1.setOccupant(hero);
        entities.add(hero);

        Tile tile2 = getTile(3, 3);
        Entity companion = new PlayerEntity("Companion", new Carrier());
        companion.getJob().applyInitialSkills(companion.getStats());
        tile2.setOccupant(companion);
        companion.getPersonalInventory().addItem(new Antidote(), 2);
        companion.getPersonalInventory().addItem(new HealingPotion(), 2);
        entities.add(companion);

        Tile tile3 = getTile(8, 8);
        Entity enemy = new EnemyEntity("Enemy Apprentice", new Apprentice());
        tile3.setOccupant(enemy);
        entities.add(enemy);

        Tile tile4 = getTile(9, 9);
        Entity enemy2 = new EnemyEntity("Enemy Carrier", new Carrier());
        tile4.setOccupant(enemy2);
        entities.add(enemy2);
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

    private void renderStatusBars(SpriteBatch batch) {
        LinkedList<Entity> sorted = new LinkedList<>(entities);
        sorted.sort(byWorldY(this));
        shapeRenderer.setProjectionMatrix(batch.getProjectionMatrix());
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        for (Entity entity : sorted) {
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
