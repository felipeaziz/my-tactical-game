package com.ibra.tacticalrpg.map;

import com.ibra.tacticalrpg.entities.Entity;

import java.util.ArrayList;
import java.util.List;

public class GameMap {
    private final Tile[][] grid;
    private final int width;
    private final int height;

    public GameMap(int width, int height) {
        this.width = width;
        this.height = height;
        this.grid = new Tile[width][height];
        initializeGrid();
    }

    private void initializeGrid() {
        for (int x = 0; x < width; x++) {
            TerrainType terrainType = TerrainType.NORMAL;
            for (int y = 0; y < height; y++) {
                if(y == 0 || y == height - 1 || x == 0 || x == width - 1) {
                    terrainType = TerrainType.WALL; // Set walls around the edges
                } else if (y == 4) {
                    terrainType = TerrainType.WATER;
                } else if(y == 3 && x == 3) {
                    terrainType = TerrainType.SWAMP;
                } else if(y == 5 && x == 3) {
                    terrainType = terrainType.TREE;
                } else {
                    terrainType = TerrainType.NORMAL;
                }

                grid[x][y] = new Tile(x, y, terrainType);
            }
        }
    }

    public List<Entity> getAllEntities() {
        List<Entity> entities = new ArrayList<>();
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (grid[x][y].isOccupied()) {
                    entities.add(grid[x][y].getOccupant());
                }
            }
        }
        return entities;
    }

    public Tile getTile(int x, int y) {
        if (x < 0 || x >= width || y < 0 || y >= height) {
            return null;
        }
        return grid[x][y];
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}
