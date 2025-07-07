package com.ibra.tacticalrpg;

import com.ibra.tacticalrpg.controller.GameController;
import com.ibra.tacticalrpg.controller.PlayerController;
import com.ibra.tacticalrpg.controller.EventLogger;

public class GameContext {
    private final GameController gameController;
    private final PlayerController playerController;
    private final EventLogger eventLogger;

    public GameContext(GameController gameController, PlayerController playerController, EventLogger eventLogger) {
        this.gameController = gameController;
        this.playerController = playerController;
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
}

