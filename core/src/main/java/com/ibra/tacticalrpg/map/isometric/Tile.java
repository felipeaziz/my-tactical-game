package com.ibra.tacticalrpg.map.isometric;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.ibra.tacticalrpg.entities.Entity;
import com.ibra.tacticalrpg.map.HighlightType;
import com.ibra.tacticalrpg.map.TerrainType;
import com.ibra.tacticalrpg.map.terraineffect.TerrainEffect;
import com.ibra.tacticalrpg.map.terraineffect.TextureCache;

public class Tile {
    private final Vector2 gridPosition;
    private final Vector2 worldPosition;
    private Entity occupant;
    private final TerrainType terrainType;
    private boolean highlighted = false;
    private HighlightType highlightType = HighlightType.NONE;

    public Tile(Vector2 gridPosition, Vector2 worldPosition, TerrainType terrainType) {
        this.gridPosition = gridPosition;
        this.worldPosition = worldPosition;
        this.terrainType = terrainType;
        this.occupant = null; // Default to not occupied
    }

    public void renderTexture(SpriteBatch batch) {
        Texture texture = TextureCache.getTexture(terrainType);
        int texWidth = texture.getWidth();
        int texHeight = texture.getHeight();
        batch.draw(texture,
            worldPosition.x - (texWidth / 2f),
            worldPosition.y - 16,
            texWidth,
            texHeight);
    }

    public void renderOutline(ShapeRenderer shapeRenderer) {
        shapeRenderer.setColor(Color.WHITE);
        drawTileOutline(shapeRenderer);
    }

    public void renderOutlineIfHovered(ShapeRenderer shapeRenderer, OrthographicCamera camera) {
        Vector3 screenMouse = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
        Vector3 worldMouse3D = camera.unproject(screenMouse);
        Vector2 worldMouse = new Vector2(worldMouse3D.x, worldMouse3D.y);

        if (isPointInsideDiamond(worldMouse)) {
            shapeRenderer.setColor(Color.YELLOW);
            drawTileOutline(shapeRenderer);
        }
    }

    public void renderHighlightFill(ShapeRenderer shapeRenderer) {
        if (highlightType == HighlightType.NONE) return;

        shapeRenderer.setColor(highlightType.getColor());

        float halfWidth = 64f / 2f;
        float halfHeight = 32f / 2f;
        float x = worldPosition.x;
        float y = worldPosition.y;
        // Posição dos vértices do losango
        float[] vertices = new float[]{
            x, y + halfHeight,     // top
            x + halfWidth, y,      // right
            x, y - halfHeight,     // bottom
            x - halfWidth, y       // left
        };
        // Preencher com 2 triângulos
        shapeRenderer.triangle(vertices[0], vertices[1], vertices[2], vertices[3], vertices[4], vertices[5]);
        shapeRenderer.triangle(vertices[0], vertices[1], vertices[4], vertices[5], vertices[6], vertices[7]);
    }

    private void drawTileOutline(ShapeRenderer shapeRenderer) {
        float rightX = worldPosition.x + (64f / 2f);
        float bottomY = worldPosition.y - (32f / 2f);
        float leftX = worldPosition.x - (64f / 2f);
        float topY = worldPosition.y + (32f / 2f);
        shapeRenderer.line(worldPosition.x, topY, rightX, worldPosition.y);
        shapeRenderer.line(rightX, worldPosition.y, worldPosition.x, bottomY);
        shapeRenderer.line(worldPosition.x, bottomY, leftX, worldPosition.y);
        shapeRenderer.line(leftX, worldPosition.y, worldPosition.x, topY);
    }

    public boolean isPointInsideDiamond(Vector2 point) {
        float centerX = worldPosition.x;
        float centerY = worldPosition.y;

        float dx = Math.abs(point.x - centerX);
        float dy = Math.abs(point.y - centerY);

        return (dx / 32f + dy / 16f) <= 1; // fórmula para losango isométrico
    }

    public Vector2 getGridPosition() {
        return gridPosition;
    }

    public int getGridPositionX() {
        return (int) gridPosition.x;
    }

    public int getGridPositionY() {
        return (int) gridPosition.y;
    }

    public void applyEffect(Entity entity) {
        TerrainEffect effect = terrainType.getEffect();
        if (effect != null) {
            effect.applyEffect(entity);
        }
    }

    public Vector2 getWorldPosition() {
        return worldPosition;
    }

    public boolean isOccupied() {
        return this.occupant != null;
    }

    public Entity getOccupant() {
        return occupant;
    }

    public void setOccupant(Entity occupant) {
        this.occupant = occupant;
    }

    public TerrainType getTerrainType() {
        return terrainType;
    }

    public boolean isHighlighted() {
        return highlighted;
    }

    public void setHighlighted(boolean highlighted) {
        this.highlighted = highlighted;
    }

    public HighlightType getHighlightType() {
        return highlightType;
    }

    public void setHighlightType(HighlightType highlightType) {
        this.highlightType = highlightType;
    }
}
