package com.ibra.tacticalrpg.map.orthogonal;

import com.ibra.tacticalrpg.entities.Entity;
import com.ibra.tacticalrpg.map.TerrainType;
import com.ibra.tacticalrpg.map.terraineffect.TerrainEffect;

public class Tile {
    private final int x;
    private final int y;
    private final TerrainType terrainType;
    private Entity occupant;

    public Tile(int x, int y, TerrainType terrainType) {
        this.x = x;
        this.y = y;
        this.terrainType = terrainType;
    }

    public TerrainType getTerrainType() {
        return terrainType;
    }

    public boolean isOccupied() {
        return occupant != null;
    }

    public Entity getOccupant() {
        return occupant;
    }

    public void setOccupant(Entity occupant) {
        this.occupant = occupant;
    }

    public void applyEffect(Entity entity) {
        TerrainEffect effect = terrainType.getEffect();
        if (effect != null) {
            effect.applyEffect(entity);
        }
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}
