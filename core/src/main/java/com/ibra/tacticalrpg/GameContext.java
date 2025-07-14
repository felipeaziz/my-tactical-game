package com.ibra.tacticalrpg;

import com.ibra.tacticalrpg.controller.GameController;
import com.ibra.tacticalrpg.controller.PlayerController;
import com.ibra.tacticalrpg.controller.EventLogger;
import com.ibra.tacticalrpg.map.isometric.GameMap;

public class GameContext {
    private final GameController gameController;
    private final PlayerController playerController;
    private final EventLogger eventLogger;
    private final GameMap gameMap;

    public GameContext(GameController gameController, PlayerController playerController, GameMap gameMap, EventLogger eventLogger) {
        this.gameController = gameController;
        this.playerController = playerController;
        this.gameMap = gameMap;
        this.eventLogger = eventLogger;
    }

    public GameController getGameController() {
        return gameController;
    }
    public PlayerController getPlayerController() {
        return playerController;
    }
    public EventLogger getEventLogger() {
        return eventLogger;
    }

    public GameMap getGameMap() {
        return gameMap;
    }
}

