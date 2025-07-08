package com.ibra.tacticalrpg.controller;

import com.ibra.tacticalrpg.entities.EnemyEntity;
import com.ibra.tacticalrpg.entities.Entity;
import com.ibra.tacticalrpg.entities.EntityStats;
import com.ibra.tacticalrpg.entities.PlayerEntity;
import com.ibra.tacticalrpg.job.Aprentice;
import com.ibra.tacticalrpg.map.orthogonal.GameMap;

import java.util.ArrayList;
import java.util.List;

public class GameController {
    private static GameController instance;
    private GameMap grid;
    private List<Entity> entities;
    private int currentEntityIndex = 0;

    public enum GameStatus {RUNNING, PLAYER_DEFEAT, PLAYER_VICTORY}

    private GameStatus status = GameStatus.RUNNING;

    public GameController() {
        instance = this;
        initializeGame();
    }

    public static GameController getInstance() {
        return instance;
    }

    public void initializeGame() {
        grid = new GameMap(10, 10);
        entities = new ArrayList<>();
        PlayerEntity player = new PlayerEntity("Hero", new EntityStats(10, 5, 3, 0, 3, 1, 2), new Aprentice());
        PlayerEntity player2 = new PlayerEntity("Companion", new EntityStats(8, 4, 2, 0, 2, 2, 3), new Aprentice());
        EnemyEntity enemy = new EnemyEntity("Enemy 1", new EntityStats(8, 4, 2, 0, 2, 1, 3), new Aprentice());
        EnemyEntity enemy2 = new EnemyEntity("Enemy 2", new EntityStats(5, 4, 2, 0, 2, 1, 1), new Aprentice());
        EnemyEntity enemy3 = new EnemyEntity("Enemy 3", new EntityStats(4, 4, 2, 0, 2, 1, 1), new Aprentice());
        entities.add(player);
        entities.add(player2);
        entities.add(enemy);
        entities.add(enemy2);
        entities.add(enemy3);
        grid.getTile(1, 1).setOccupant(player);
        grid.getTile(1, 2).setOccupant(player2);
        grid.getTile(8, 8).setOccupant(enemy);
        grid.getTile(8, 7).setOccupant(enemy2);
        grid.getTile(8, 6).setOccupant(enemy3);
        entities.sort((e1, e2) -> Integer.compare(e2.getStats().getSpeed(), e1.getStats().getSpeed()));
        currentEntityIndex = 0;
    }

    public GameMap getGrid() {
        return grid;
    }

    public List<Entity> getEntities() {
        return entities;
    }

    public int getCurrentEntityIndex() {
        return currentEntityIndex;
    }

    public void setCurrentEntityIndex(int idx) {
        this.currentEntityIndex = idx;
    }

    public Entity getCurrentEntity() {
        if (currentEntityIndex < entities.size()) {
            return entities.get(currentEntityIndex);
        }
        return null;
    }

    public void nextEntity() {
        currentEntityIndex++;
    }

    public void resetTurn() {
        entities.forEach(Entity::resetTurn);
        currentEntityIndex = 0;
    }

    public GameStatus getGameStatus() {
        return status;
    }

    public void updateGameStatus() {
        boolean playerAlive = entities.stream()
            .filter(e -> e instanceof PlayerEntity)
            .anyMatch(Entity::isAlive);
        boolean enemyAlive = entities.stream()
            .filter(e -> e instanceof EnemyEntity)
            .anyMatch(Entity::isAlive);
        if (!playerAlive) {
            status = GameStatus.PLAYER_DEFEAT;
        } else if (!enemyAlive) {
            status = GameStatus.PLAYER_VICTORY;
        } else {
            status = GameStatus.RUNNING;
        }
    }

    public void advanceTurn() {
        while (getCurrentEntity() != null && !getCurrentEntity().isAlive()) {
            nextEntity();
        }
        if (currentEntityIndex >= entities.size()) {
            resetTurn();
            setCurrentEntityIndex(0);
            while (getCurrentEntity() != null && !getCurrentEntity().isAlive()) {
                nextEntity();
            }
            if (currentEntityIndex >= entities.size()) return;
        }
        Entity current = getCurrentEntity();
        current.takeTurn();
        if (current.isTurnDone()) {
            current.resetTurn();
            nextEntity();
            if (currentEntityIndex < entities.size()) {
                getCurrentEntity().getStats().updateEffects();
            }
        }
    }
}
