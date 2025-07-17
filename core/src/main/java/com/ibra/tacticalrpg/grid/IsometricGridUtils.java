package com.ibra.tacticalrpg.grid;

import com.ibra.tacticalrpg.entities.Entity;
import com.ibra.tacticalrpg.map.isometric.GameMap;
import com.ibra.tacticalrpg.map.isometric.Tile;

import java.util.*;

public class IsometricGridUtils {
    private static final float TILE_WIDTH = 64f;
    private static final float TILE_HEIGHT = 32f;

    public static float getTileWidth() {
        return TILE_WIDTH;
    }

    public static float getTileHeight() {
        return TILE_HEIGHT;
    }

    /**
     * Finds a path from start to end using the A* algorithm.
     *
     * @param grid The game map containing the tiles.
     * @param start The starting tile.
     * @param end The ending tile.
     * @return A list of tiles representing the path, or an empty list if no path is found.
     */
    public static List<Tile> findPath(GameMap grid, Tile start, Tile end) {
        if (start == null || end == null) return Collections.emptyList();

        Map<Tile, Tile> cameFrom = new HashMap<>();
        Map<Tile, Integer> gScore = new HashMap<>();
        Map<Tile, Integer> fScore = new HashMap<>();

        Comparator<Tile> comparator = Comparator.comparingInt(fScore::get);
        PriorityQueue<Tile> openSet = new PriorityQueue<>(comparator);
        Set<Tile> openSetLookup = new HashSet<>();

        gScore.put(start, 0);
        fScore.put(start, heuristic(start, end));

        openSet.add(start);
        openSetLookup.add(start);

        while (!openSet.isEmpty()) {
            Tile current = openSet.poll();
            openSetLookup.remove(current);

            if (current.equals(end)) {
                return reconstructPath(cameFrom, current);
            }

            for (Tile neighbor : getValidNeighbors(grid, current)) {
                int tentativeGScore = gScore.get(current) + neighbor.getTerrainType().getMovementCost();

                if (tentativeGScore < gScore.getOrDefault(neighbor, Integer.MAX_VALUE)) {
                    cameFrom.put(neighbor, current);
                    gScore.put(neighbor, tentativeGScore);
                    fScore.put(neighbor, tentativeGScore + heuristic(neighbor, end));

                    if (!openSetLookup.contains(neighbor)) {
                        openSet.add(neighbor);
                        openSetLookup.add(neighbor);
                    }
                }
            }
        }

        return Collections.emptyList();
    }

    private static List<Tile> getValidNeighbors(GameMap grid, Tile cell) {
        List<Tile> neighbors = new ArrayList<>();
        int[][] directions = {
            {1, 0}, {-1, 0}, {0, 1}, {0, -1}
        };

        for (int[] dir : directions) {
            int newX = cell.getGridPositionX() + dir[0];
            int newY = cell.getGridPositionY() + dir[1];
            Tile neighbor = grid.getTile(newX, newY);
            if (neighbor != null && !grid.isTileBlocked(neighbor.getGridPositionX(), neighbor.getGridPositionY())) {
                if (!neighbor.isOccupied() || !neighbor.getOccupant().isAlive()) {
                    neighbors.add(neighbor);
                }
            }
        }

        return neighbors;
    }

    private static int heuristic(Tile a, Tile b) {
        return Math.abs(a.getGridPositionX() - b.getGridPositionX())
            + Math.abs(a.getGridPositionY() - b.getGridPositionY());
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

    public static Tile findEntityTile(GameMap map, Entity entity) {
        return map.getBaseTiles().stream()
            .filter(tile -> entity.equals(tile.getOccupant()))
            .findAny().orElse(null);
    }

    public static Comparator<Entity> byWorldY(GameMap map) {
        return Comparator.comparing(entity -> {
            Tile tile = findEntityTile(map, entity);
            return tile != null ? tile.getWorldPosition().y : 0;
        });
    }

    public static float calculateWorldPositionY(int col, int row) {
        return (col + row) * (TILE_HEIGHT / 2f);
    }

    public static float calculateWorldPositionX(int col, int row) {
        return (col - row) * (TILE_WIDTH / 2f);
    }
}
