package com.ibra.tacticalrpg.entities;

import com.badlogic.gdx.graphics.Texture;
import com.ibra.tacticalrpg.GameContext;
import com.ibra.tacticalrpg.action.Action;
import com.ibra.tacticalrpg.controller.EventLogger;
import com.ibra.tacticalrpg.controller.GameController;
import com.ibra.tacticalrpg.controller.PlayerActionType;
import com.ibra.tacticalrpg.controller.PlayerController;
import com.ibra.tacticalrpg.job.Job;

public class PlayerEntity extends Entity {
    private Action currentAction = null;
    private boolean actionDone = false;
    private transient Texture texture;

    private PlayerActionType currentActionType = PlayerActionType.NONE;

    public PlayerEntity(String name, Job job) {
        super(name, job);
        this.texture = new Texture("job/player/" + job.getName().toLowerCase() + ".png");
    }

    @Override
    public void takeTurn() {
        if (gameContext == null) return;
        GameController gameController = gameContext.getGameController();
        PlayerController playerController = gameContext.getPlayerController();
        EventLogger logger = gameContext.getEventLogger();

        if (playerController != null && logger != null && gameController != null) {
            playerController.handleInput(gameContext.getGameMap(),
                gameController.getEntities(),
                logger,
                this,
                gameContext.getCamera());
        }
    }

    @Override
    public boolean isTurnDone() {
        return actionDone && !isMoving();
    }

    @Override
    public void resetTurn() {
        super.resetTurn();
        setActionDone(false);
        setCurrentAction(null);
    }

    @Override
    public Texture getTexture() {
        return texture;
    }

    public Action getCurrentAction() {
        return currentAction;
    }

    public void setCurrentAction(Action currentAction) {
        this.currentAction = currentAction;
    }

    public boolean isActionDone() {
        return actionDone;
    }

    public void setActionDone(boolean actionDone) {
        this.actionDone = actionDone;
        if (actionDone) {
            movedThisTurn = true;
            tookActionThisTurn = true;
        }
    }

    public PlayerActionType getCurrentActionType() {
        return currentActionType;
    }

    public void setCurrentActionType(PlayerActionType currentActionType) {
        this.currentActionType = currentActionType;
    }
}
