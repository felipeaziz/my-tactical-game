package com.ibra.tacticalrpg.controller;

import com.ibra.tacticalrpg.entities.EnemyEntity;
import com.ibra.tacticalrpg.entities.Entity;
import com.ibra.tacticalrpg.entities.PlayerEntity;

import java.util.Collections;
import java.util.List;

public class GameController {
    private static GameController instance;
    private List<Entity> entities = Collections.emptyList();
    private int currentEntityIndex = 0;
    private EventLogger logger;

    public enum GameStatus {RUNNING, PLAYER_DEFEAT, PLAYER_VICTORY}

    private GameStatus status = GameStatus.RUNNING;

    public GameController() {
        instance = this;
    }

    public void setup(List<Entity> entityList) {
        this.entities = entityList;
        this.entities.sort((e1, e2) -> Integer.compare(e2.getStats().getSpeed(), e1.getStats().getSpeed()));
        this.updateGameStatus();
        currentEntityIndex = 0;
    }

    public static GameController getInstance() {
        return instance;
    }

    public List<Entity> getEntities() {
        return Collections.unmodifiableList(entities);
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

    public void resetTurns() {
        entities.forEach(Entity::resetTurn);
    }

    public GameStatus getGameStatus() {
        return status;
    }

    public void handleTurns() {
        updateGameStatus();
        if (status != GameStatus.RUNNING) return;

        Entity current = getCurrentEntity();
        // Ignora entidade morta
        if (current == null || !current.isAlive()) {
            advanceTurn();
            return;
        }
        // Espera movimento terminar
        if (current.isMoving()) {
            return;
        }
        // Se ainda não agiu, permite agir
        if (!current.isTurnDone()) {
            current.takeTurn();
        } else {
            current.getStats().updateEffects();
            advanceTurn();
        }
    }

    private void advanceTurn() {
        do {
            nextEntity(); // Avança para o próximo
        } while (getCurrentEntity() != null && !getCurrentEntity().isAlive());

        // Reinicia a rodada quando chega ao final
        if (currentEntityIndex >= entities.size()) {
            resetTurns();
            currentEntityIndex = 0;
        }
        Entity current = getCurrentEntity();
        if (current != null && current.isAlive() && logger != null) {
            logger.log("É a vez de " + current.getName() + ".");
        }
    }

    private void updateGameStatus() {
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

    public void setLogger(EventLogger logger) {
        this.logger = logger;
    }
}
