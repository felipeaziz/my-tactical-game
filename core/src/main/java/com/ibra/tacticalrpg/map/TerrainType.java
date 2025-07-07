package com.ibra.tacticalrpg.map;


import com.ibra.tacticalrpg.map.terraineffect.BurnEffect;
import com.ibra.tacticalrpg.map.terraineffect.PoisonEffect;
import com.ibra.tacticalrpg.map.terraineffect.TerrainEffect;

public enum TerrainType {
    NORMAL(false, 1, null, "normal.PNG"),
    WATER(false, 2, null, "water.PNG"),
    ROCK(true, Integer.MAX_VALUE, null, "rock.PNG"),
    TREE(true, Integer.MAX_VALUE, null, "tree.PNG"),
    GRASS(false, 1, null, "grass.PNG"),
    SAND(false, 1, null, "desert.PNG"),
    SNOW(false, 1, null, "snow.PNG"),
    SWAMP(false, 2, new PoisonEffect(), "swamp.PNG"),
    LAVA(true, Integer.MAX_VALUE, new BurnEffect(), "lava.PNG"),
    WALL(true, Integer.MAX_VALUE, null, "rock.PNG"); //temporary texture for walls

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
