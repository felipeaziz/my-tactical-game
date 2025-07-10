package com.ibra.tacticalrpg.map;


import com.ibra.tacticalrpg.map.terraineffect.BurnEffect;
import com.ibra.tacticalrpg.map.terraineffect.PoisonEffect;
import com.ibra.tacticalrpg.map.terraineffect.TerrainEffect;

public enum TerrainType {
    NORMAL(false, 1, null, "normal.png"),
    WATER(false, 2, null, "water.png"),
    ROCK(true, Integer.MAX_VALUE, null, "rock.png"),
    TREE(true, Integer.MAX_VALUE, null, "tree.png"),
    GRASS(false, 1, null, "grass.png"),
    SAND(false, 1, null, "sand.png"),
    SNOW(false, 1, null, "snow.png"),
    SWAMP(false, 2, new PoisonEffect(), "swamp.png"),
    LAVA(true, Integer.MAX_VALUE, new BurnEffect(), "lava.png"),
    WALL(true, Integer.MAX_VALUE, null, "rock.png"); //temporary texture for walls

    private final boolean isObstacle;
    private final int movementCost;
    private final TerrainEffect effect;
    private final String textureFileName;

    TerrainType(boolean isObstacle, int movementCost, TerrainEffect effect, String textureFileName) {
        this.isObstacle = isObstacle;
        this.movementCost = movementCost;
        this.effect = effect;
        this.textureFileName = textureFileName;
    }

    public boolean isObstacle() {
        return isObstacle;
    }

    public int getMovementCost() {
        return movementCost;
    }

    public TerrainEffect getEffect() {
        return effect;
    }

    public String getTextureFileName() {
        return textureFileName;
    }
}
