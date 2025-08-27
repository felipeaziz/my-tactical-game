package com.ibra.tacticalrpg;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.ibra.tacticalrpg.controller.CameraController;
import com.ibra.tacticalrpg.controller.GameController;
import com.ibra.tacticalrpg.controller.PlayerController;
import com.ibra.tacticalrpg.controller.EventLogger;
import com.ibra.tacticalrpg.map.isometric.GameMap;
import com.ibra.tacticalrpg.ui.GameUIRenderer;

public class GameContext {
    private final GameController gameController;
    private final PlayerController playerController;
    private final EventLogger eventLogger;
    private final GameMap gameMap;
    private final CameraController cameraController;
    private final GameUIRenderer uiRenderer;

    public GameContext(GameController gameController,
                       PlayerController playerController,
                       CameraController cameraController,
                       GameMap gameMap,
                       GameUIRenderer uiRenderer,
                       EventLogger eventLogger) {
        this.gameController = gameController;
        this.playerController = playerController;
        this.cameraController = cameraController;
        this.gameMap = gameMap;
        this.uiRenderer = uiRenderer;
        this.eventLogger = eventLogger;
    }

    public GameController getGameController() {
        return gameController;
    }
    public PlayerController getPlayerController() {
        return playerController;
    }
    public CameraController getCameraController() {
        return cameraController;
    }
    public EventLogger getEventLogger() {
        return eventLogger;
    }
    public GameMap getGameMap() {
        return gameMap;
    }
    public OrthographicCamera getCamera() {
        return cameraController.getCamera();
    }
    public GameUIRenderer getUiRenderer() {
        return uiRenderer;
    }
}

