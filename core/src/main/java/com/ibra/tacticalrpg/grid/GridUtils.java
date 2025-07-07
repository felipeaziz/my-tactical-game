package com.ibra.tacticalrpg.grid;

import com.ibra.tacticalrpg.entities.Entity;
import com.ibra.tacticalrpg.controller.GameController;
import com.ibra.tacticalrpg.map.GameMap;
import com.ibra.tacticalrpg.map.Tile;

import java.util.*;

public class GridUtils {
    public static List<Tile> findPath(GameMap grid, Tile start, Tile end) {
        if (start == null || end == null) return new ArrayList<>();

        Map<Tile, Tile> cameFrom = new HashMap<>();
        Map<Tile, Integer> gScore = new HashMap<>();
        Map<Tile, Integer> fScore = new HashMap<>();
        PriorityQueue<Tile> openSet = new PriorityQueue<>(
            Comparator.comparingInt(a -> fScore.getOrDefault(a, Integer.MAX_VALUE))
        );

        gScore.put(start, 0);
        fScore.put(start, heuristic(start, end));
        openSet.add(start);

        while (!openSet.isEmpty()) {
            Tile current = openSet.poll();
            if (current == end) {
                return reconstructPath(cameFrom, current);
            }

            for (Tile neighbor : getValidNeighbors(grid, current)) {
                if (neighbor.isOccupied() || neighbor.getTerrainType().isObstacle()) {
                    continue; // Skip occupied cells except for living entities
                }
                int moveCost = neighbor.getTerrainType().getMovementCost();
                int tentativeGScore = gScore.get(current) + moveCost;
                if (tentativeGScore < gScore.getOrDefault(neighbor, Integer.MAX_VALUE)) {
                    cameFrom.put(neighbor, current);
                    gScore.put(neighbor, tentativeGScore);
                    fScore.put(neighbor, tentativeGScore + heuristic(neighbor, end));
                    if (!openSet.contains(neighbor)) {
                        openSet.add(neighbor);
                    }
                }
            }
        }
        return new ArrayList<>();
    }

    private static List<Tile> getValidNeighbors(GameMap grid, Tile cell) {
        List<Tile> neighbors = new ArrayList<>();
        int[][] directions = {{0,1}, {1,0}, {0,-1}, {-1,0}};

        for (int[] dir : directions) {
            int newX = cell.getX() + dir[0];
            int newY = cell.getY() + dir[1];
            Tile neighbor = grid.getTile(newX, newY);
            if (neighbor != null) {
                if (!neighbor.isOccupied() || !neighbor.getOccupant().isAlive()) {
                    neighbors.add(neighbor);
                }
            }
        }

        return neighbors;
    }

    private static int heuristic(Tile a, Tile b) {
        return Math.abs(a.getX() - b.getX()) + Math.abs(a.getY() - b.getY());
    }

    private static List<Tile> reconstructPath(Map<Tile, Tile> cameFrom, Tile current) {
        List<Tile> path = new ArrayList<>();
        path.add(current);

        while (cameFrom.containsKey(current)) {
            current = cameFrom.get(current);
            path.add(0, current);
        }

        return path;
    }

    public static GameMap getGrid(Entity entity) {
        GameMap map = findEntityMap(entity);
        if (map == null) {
            throw new IllegalStateException("Entity " + entity.getName() + " não está em nenhum mapa");
        }
        return map;
    }

    private static GameMap findEntityMap(Entity entity) {
        GameMap map = GameController.getInstance().getGrid();
        Tile tile = findEntityTile(map, entity);
        return tile != null ? map : null;
    }

    public static Tile findEntityTile(GameMap map, Entity entity) {
        for(int x = 0; x < map.getWidth(); x++) {
            for (int y = 0; y < map.getHeight(); y++) {
                Tile tile = map.getTile(x, y);
                if (tile != null && tile.isOccupied() && tile.getOccupant().equals(entity)) {
                    return tile;
                }
            }
        }
        return null; // Entity not found
    }
}
