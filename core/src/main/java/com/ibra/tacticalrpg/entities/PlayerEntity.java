package com.ibra.tacticalrpg.entities;

import com.ibra.tacticalrpg.GameContext;
import com.ibra.tacticalrpg.action.Action;
import com.ibra.tacticalrpg.controller.EventLogger;
import com.ibra.tacticalrpg.controller.GameController;
import com.ibra.tacticalrpg.controller.PlayerController;
import com.ibra.tacticalrpg.job.Job;

public class PlayerEntity extends Entity {
    private transient GameContext gameContext;
    private Action currentAction = null;
    private boolean actionDone = false;

    public PlayerEntity(String name, Job job) {
        super(name, job);
    }

    public void setGameContext(GameContext gameContext) {
        this.gameContext = gameContext;
    }

    @Override
    public void takeTurn() {
        if (gameContext == null) return;
        GameController gameController = gameContext.getGameController();
        PlayerController playerController = gameContext.getPlayerController();
        EventLogger logger = gameContext.getEventLogger();

        if (playerController != null && logger != null && gameController != null) {
            playerController.handleInput(gameController.getGrid(), gameController.getEntities(), logger, this);
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
}
