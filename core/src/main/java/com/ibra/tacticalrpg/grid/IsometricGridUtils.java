package com.ibra.tacticalrpg.grid;

import com.badlogic.gdx.math.Vector2;
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
     * @param grid  The game map containing the tiles.
     * @param start The starting tile.
     * @param end   The ending tile.
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

    public static Vector2 worldToScreenIso(int gridX, int gridY) {
        float screenX = calculateWorldPositionX(gridX, gridY);
        float screenY = calculateWorldPositionY(gridX, gridY);
        return new Vector2(screenX, screenY);
    }

    public static float calculateWorldPositionY(int col, int row) {
        return (col + row) * (TILE_HEIGHT / 2f);
    }

    public static float calculateWorldPositionX(int col, int row) {
        return (col - row) * (TILE_WIDTH / 2f);
    }

    /**
     * Útil para skills de área (AOE).
     *
     * @param grid       O mapa do jogo
     * @param centerTile O tile central da área
     * @param radius     O raio da área
     * @return Lista de tiles dentro do raio
     */
    public static List<Tile> getTilesInRange(GameMap grid, Tile centerTile, int radius) {
        List<Tile> tilesInRange = new ArrayList<>();
        if (centerTile == null || radius <= 0) {
            return tilesInRange;
        }
        int centerX = centerTile.getGridPositionX();
        int centerY = centerTile.getGridPositionY();
        // Percorre uma área quadrada maior que o raio e filtra pela distância
        for (int x = centerX - radius; x <= centerX + radius; x++) {
            for (int y = centerY - radius; y <= centerY + radius; y++) {
                Tile tile = grid.getTile(x, y);
                if (tile != null) {
                    int distance = heuristic(centerTile, tile);
                    if (distance <= radius) {
                        tilesInRange.add(tile);
                    }
                }
            }
        }
        return tilesInRange;
    }

    /**
     * Encontra todos os tiles em uma linha reta a partir de um ponto inicial em direção a um tile alvo.
     * Útil para skills que afetam uma linha (raio, lança, etc.).
     * @param grid          O mapa do jogo
     * @param start         Tile inicial
     * @param targetTile    Direção da linha
     * @param maxLength     Comprimento da linha
     * @return Lista de tiles na linha
     */
    public static List<Tile> getTilesInLineDirection(GameMap grid, Tile start, Tile targetTile, int maxLength) {
        List<Tile> tilesInLine = new ArrayList<>();
        if (start == null || targetTile == null || maxLength <= 0) {
            return tilesInLine;
        }
        // Calcula a direção
        int x0 = start.getGridPositionX();
        int y0 = start.getGridPositionY();
        int x1 = targetTile.getGridPositionX();
        int y1 = targetTile.getGridPositionY();
        // Normaliza a direção para ter incrementos de -1, 0 ou 1
        int dx = Integer.compare(x1 - x0, 0);
        int dy = Integer.compare(y1 - y0, 0);
        int x = x0;
        int y = y0;
        // Inclui o tile inicial
        tilesInLine.add(start);
        // Continua na mesma direção até atingir maxLength
        for (int step = 1; step < maxLength; step++) {
            x += dx;
            y += dy;
            Tile nextTile = grid.getTile(x, y);
            if (nextTile == null) {
                break;
            }
            tilesInLine.add(nextTile);
        }

        return tilesInLine;
    }

    /**
     * Encontra todos os tiles em uma linha reta entre dois pontos.
     * Útil para skills que afetam uma linha (raio, lança, etc.).
     *
     * @param grid      O mapa do jogo
     * @param start     Tile inicial
     * @param end       Tile final
     * @param maxLength Comprimento máximo da linha (para limitar alcance)
     * @return Lista de tiles na linha
     */
    public static List<Tile> getTilesInLine(GameMap grid, Tile start, Tile end, int maxLength) {
        List<Tile> tilesInLine = new ArrayList<>();
        if (start == null || end == null) {
            return tilesInLine;
        }
        int x0 = start.getGridPositionX();
        int y0 = start.getGridPositionY();
        int x1 = end.getGridPositionX();
        int y1 = end.getGridPositionY();
        // Implementação do algoritmo de Bresenham para linha em grid
        int dx = Math.abs(x1 - x0);
        int dy = Math.abs(y1 - y0);
        int x = x0;
        int y = y0;
        int n = 1 + dx + dy;
        int x_inc = (x1 > x0) ? 1 : -1;
        int y_inc = (y1 > y0) ? 1 : -1;
        int error = dx - dy;
        dx *= 2;
        dy *= 2;
        for (; n > 0; --n) {
            Tile tile = grid.getTile(x, y);
            if (tile != null) {
                tilesInLine.add(tile);

                // Limita o comprimento da linha
                if (tilesInLine.size() >= maxLength) {
                    break;
                }
            }
            if (error > 0) {
                x += x_inc;
                error -= dy;
            } else {
                y += y_inc;
                error += dx;
            }
        }
        return tilesInLine;
    }

    /**
     * Versão sobrecarregada de getTilesInLine sem limite de comprimento.
     */
    public static List<Tile> getTilesInLine(GameMap grid, Tile start, Tile end) {
        int maxDistance = heuristic(start, end) + 1; // +1 para garantir que chegue ao final
        return getTilesInLine(grid, start, end, maxDistance);
    }

    /**
     * Encontra tiles em um cone a partir de um ponto inicial.
     * Útil para skills como "breath attacks" ou ataques direcionais.
     *
     * @param grid      O mapa do jogo
     * @param origin    Tile de origem do cone
     * @param direction Direção do cone (0=Norte, 1=Leste, 2=Sul, 3=Oeste)
     * @param range     Alcance do cone
     * @param width     Largura do cone (em tiles)
     * @return Lista de tiles no cone
     */
    public static List<Tile> getTilesInCone(GameMap grid, Tile origin, int direction, int range, int width) {
        List<Tile> tilesInCone = new ArrayList<>();
        if (origin == null || range <= 0) {
            return tilesInCone;
        }
        int originX = origin.getGridPositionX();
        int originY = origin.getGridPositionY();
        // Direções: 0=Norte(-Y), 1=Leste(+X), 2=Sul(+Y), 3=Oeste(-X)
        int[] dx = {0, 1, 0, -1};
        int[] dy = {-1, 0, 1, 0};
        for (int distance = 1; distance <= range; distance++) {
            // Calcula a largura do cone nesta distância
            int currentWidth = Math.min(width, distance);
            for (int offset = -currentWidth; offset <= currentWidth; offset++) {
                int targetX, targetY;
                if (direction % 2 == 0) { // Norte ou Sul
                    targetX = originX + offset;
                    targetY = originY + (dy[direction] * distance);
                } else { // Leste ou Oeste
                    targetX = originX + (dx[direction] * distance);
                    targetY = originY + offset;
                }
                Tile tile = grid.getTile(targetX, targetY);
                if (tile != null) {
                    tilesInCone.add(tile);
                }
            }
        }
        return tilesInCone;
    }

    /**
     * Verifica se há linha de visão clara entre dois tiles.
     * Útil para skills que requerem line of sight.
     *
     * @param grid  O mapa do jogo
     * @param start Tile inicial
     * @param end   Tile final
     * @return true se há linha de visão clara
     */
    public static boolean hasLineOfSight(GameMap grid, Tile start, Tile end) {
        List<Tile> tilesInLine = getTilesInLine(grid, start, end);
        // Remove o tile inicial e final da verificação
        tilesInLine.remove(start);
        tilesInLine.remove(end);
        // Verifica se algum tile no caminho está bloqueado
        for (Tile tile : tilesInLine) {
            if (grid.isTileBlocked(tile.getGridPositionX(), tile.getGridPositionY()) ||
                (tile.isOccupied() && tile.getOccupant().isAlive())) {
                return false;
            }
        }
        return true;
    }

    /**
     * Encontra todos os tiles em um anel (donut) ao redor de um ponto.
     * Útil para skills que afetam apenas a borda de uma área.
     *
     * @param grid        O mapa do jogo
     * @param centerTile  O tile central
     * @param innerRadius Raio interno (tiles dentro deste raio não são incluídos)
     * @param outerRadius Raio externo
     * @return Lista de tiles no anel
     */
    public static List<Tile> getTilesInRing(GameMap grid, Tile centerTile, int innerRadius, int outerRadius) {
        List<Tile> tilesInRing = new ArrayList<>();
        if (centerTile == null || outerRadius <= innerRadius) {
            return tilesInRing;
        }
        int centerX = centerTile.getGridPositionX();
        int centerY = centerTile.getGridPositionY();
        for (int x = centerX - outerRadius; x <= centerX + outerRadius; x++) {
            for (int y = centerY - outerRadius; y <= centerY + outerRadius; y++) {
                Tile tile = grid.getTile(x, y);
                if (tile != null) {
                    int distance = heuristic(centerTile, tile);
                    if (distance > innerRadius && distance <= outerRadius) {
                        tilesInRing.add(tile);
                    }
                }
            }
        }
        return tilesInRing;
    }
}
