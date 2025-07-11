package com.ibra.tacticalrpg.entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.ibra.tacticalrpg.item.Item;
import com.ibra.tacticalrpg.job.Job;
import com.ibra.tacticalrpg.map.orthogonal.GameMap;
import com.ibra.tacticalrpg.map.orthogonal.Tile;

import java.util.*;

import static com.ibra.tacticalrpg.grid.GridUtils.findEntityTile;

public abstract class Entity {
    protected final String name;
    protected int level = 1;
    protected EntityStats stats;
    protected Job job;
    protected List<Item> inventory;

    protected boolean movedThisTurn = false;
    protected boolean tookActionThisTurn = false;  // mudando de actedThisTurn para tookActionThisTurn

    // Movimentação
    protected List<Tile> movePath = new ArrayList<>();
    protected float moveProgress = 0f;
    protected static final float MOVE_SPEED = 6f;

    public Entity(String name, Job job) {
        this.name = name;
        this.job = job;
        this.stats = job.applyInitialStats();
        this.inventory = new ArrayList<>();
    }

    public abstract void takeTurn();

    public abstract Texture getTexture();

    public void render(SpriteBatch batch, Vector2 worldPosition) {
        batch.draw(getTexture(),
            worldPosition.x - (getTexture().getWidth() / 2f),
            worldPosition.y - 3,
            getTexture().getWidth(),
            getTexture().getHeight());
    }

    public void renderStatusBars(ShapeRenderer shapeRenderer, Vector2 worldPosition) {
        float barWidth = 32f;
        float barHeight = 4f;
        float spacing = 2f;

        float baseX = worldPosition.x - barWidth / 2f;
        float baseY = worldPosition.y + getSpriteHeight() + 4f; // pode ajustar conforme o sprite

        // HP
        float hpRatio = (float) stats.getCurrentHp() / stats.getMaxHp();
        shapeRenderer.setColor(Color.DARK_GRAY);
        shapeRenderer.rect(baseX, baseY, barWidth, barHeight);
        shapeRenderer.setColor(Color.RED);
        shapeRenderer.rect(baseX, baseY, barWidth * hpRatio, barHeight);

        // MP
        float mpRatio = (float) stats.getCurrentMp() / stats.getMaxMp();
        shapeRenderer.setColor(Color.DARK_GRAY);
        shapeRenderer.rect(baseX, baseY - (barHeight + spacing), barWidth, barHeight);
        shapeRenderer.setColor(Color.BLUE);
        shapeRenderer.rect(baseX, baseY - (barHeight + spacing), barWidth * mpRatio, barHeight);

        shapeRenderer.setColor(Color.WHITE); // reset color
    }


    public void levelUp() {
        level++;
//        stats.increaseStatsForLevelUp();
        stats.setCurrentHp(stats.getMaxHp()); // Restaura HP ao subir de nível
        // Aqui você pode adicionar lógica para aumentar outras estatísticas, como ataque, defesa, etc.
    }

    public Set<Tile> getMovableCells(GameMap grid) {
        Set<Tile> reachable = new HashSet<>();
        Queue<Tile> queue = new LinkedList<>();
        Map<Tile, Integer> distance = new HashMap<>();
        Tile start = findEntityTile(grid, this);
        queue.add(start);
        distance.put(start, 0);

        while (!queue.isEmpty()) {
            Tile current = queue.poll();
            int dist = distance.get(current);
            if (dist > 0 && !current.isOccupied()) {
                reachable.add(current);
            }
            if (dist == this.stats.getMoveRange()) continue;
            for (int[] d : new int[][]{{1, 0}, {-1, 0}, {0, 1}, {0, -1}}) {
                Tile neighbor = grid.getTile(current.getX() + d[0], current.getY() + d[1]);
                if (neighbor == null ||
                    neighbor.getTerrainType().isObstacle() ||
                    (neighbor.isOccupied() && neighbor.getOccupant() != this)) {
                    continue;
                }
                int totalCost = dist + neighbor.getTerrainType().getMovementCost();
                if (totalCost > this.stats.getMoveRange()) {
                    continue; // Não ultrapassar o alcance de movimento
                }
                if (!distance.containsKey(neighbor) || totalCost < distance.get(neighbor)) {
                    distance.put(neighbor, totalCost);
                    queue.add(neighbor);
                }
            }
        }
        return reachable;
    }

    public Set<Tile> getAttackableCells(GameMap grid) {
        Set<Tile> attackable = new HashSet<>();
        Tile start = findEntityTile(grid, this);
        if (start == null) return attackable;

        int range = stats.getAttackRange();
        for (int dx = -range; dx <= range; dx++) {
            for (int dy = -range; dy <= range; dy++) {
                if (Math.abs(dx) + Math.abs(dy) <= range) {
                    Tile cell = grid.getTile(start.getX() + dx, start.getY() + dy);
                    if (cell != null && cell != start) {
                        attackable.add(cell);
                    }
                }
            }
        }
        return attackable;
    }

    public boolean isMoving() {
        return !movePath.isEmpty();
    }

    public void updateMovement(GameMap grid, float delta) {
        if (!movePath.isEmpty()) {
            moveProgress += delta * MOVE_SPEED;
            if (moveProgress >= 1f) {
                moveProgress = 0f;
                Tile next = movePath.remove(0);
                if (next.getTerrainType().isObstacle()) {
                    movePath.clear();
                    return;
                }
                Tile current = findEntityTile(grid, this);
                if (current != null) {
                    current.setOccupant(null);
                }
                next.setOccupant(this);
                if (!isMoving()) {
                    next.applyEffect(this);
                }
            }
        }
    }

    public boolean isAlive() {
        return stats.getCurrentHp() > 0;
    }

    public void resetTurn() {
        movedThisTurn = false;
        tookActionThisTurn = false;
    }

    public boolean isTurnDone() {
        return movedThisTurn && tookActionThisTurn && !isMoving();
    }

    // Getters e Setters
    public String getName() {
        return name;
    }

    public EntityStats getStats() {
        return stats;
    }

    public boolean hasMoved() {
        return movedThisTurn;
    }

    public void setMovedThisTurn(boolean movedThisTurn) {
        this.movedThisTurn = movedThisTurn;
    }

    public boolean hasActed() {
        return tookActionThisTurn;
    }

    public void setActedThisTurn(boolean acted) {
        this.tookActionThisTurn = acted;
    }

    public void setMovePath(List<Tile> path) {
        this.movePath.clear();
        this.movePath.addAll(path);
    }

    protected int getSpriteHeight() {
        return getTexture().getHeight();
    }
}
