package com.ibra.tacticalrpg.isometric;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.ibra.tacticalrpg.BattleScreen;
import com.ibra.tacticalrpg.GameContext;
import com.ibra.tacticalrpg.TacticalRPG;
import com.ibra.tacticalrpg.controller.CameraController;
import com.ibra.tacticalrpg.controller.EventLogger;
import com.ibra.tacticalrpg.controller.GameController;
import com.ibra.tacticalrpg.controller.PlayerController;
import com.ibra.tacticalrpg.entities.Entity;
import com.ibra.tacticalrpg.entities.PlayerEntity;
import com.ibra.tacticalrpg.job.Apprentice;
import com.ibra.tacticalrpg.map.isometric.GameMap;
import com.ibra.tacticalrpg.ui.EventLog;
import com.ibra.tacticalrpg.ui.GameUIRenderer;

import static com.badlogic.gdx.graphics.GL20.GL_COLOR_BUFFER_BIT;
import static com.badlogic.gdx.graphics.GL20.GL_DEPTH_BUFFER_BIT;

public class GameScreen extends ScreenAdapter implements EventLogger {
    private static final int LOG_SIZE = 5;

    private final TacticalRPG game;
    private final OrthographicCamera camera;
    private final CameraController cameraController;
    private final GameMap map;
    private final GameUIRenderer uiRenderer;
    private final BitmapFont font;
    private EventLog eventLog;
    private GameContext gameContext;

    private enum TurnState { PLAYER, GAME_OVER }
    private TurnState turnState = TurnState.PLAYER;

    public GameScreen(TacticalRPG game) {
        this.game = game;
        this.camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.position.set(0, (9 + 9) * 32 / 4f, 0); // Start at the origin
        cameraController = new CameraController(camera);
        this.map = new GameMap();

        this.font = new BitmapFont();
        this.uiRenderer = new GameUIRenderer(font);
        initializeGame();
    }

    private void initializeGame() {
        this.eventLog = new EventLog(LOG_SIZE);
        this.gameContext = new GameContext(new GameController(), new PlayerController(), this.map, this);
        GameController controller = new GameController();
        controller.setup(map.getEntities());
        turnState = TurnState.PLAYER;
        for (Entity ent : gameContext.getGameController().getEntities()) {
            if (ent instanceof PlayerEntity) {
                PlayerEntity player = (PlayerEntity) ent;
                player.setGameContext(gameContext);
            }
        }
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 0);// Set the clear color to black with full opacity
        Gdx.gl.glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);// Clear the screen
        game.getBatch().setProjectionMatrix(camera.combined);

        cameraController.update();

        map.render(game.getBatch(), camera);
        //menu fixo e passando player temporário. futuramente o menu será renderizado ao lado do currentPlayer
        uiRenderer.renderActionMenu(game.getBatch(), camera, new PlayerEntity("menu", new Apprentice()));
//        checkEndGame();
//        updateEntityMovement(delta);
        handleTurns();
    }

    private void handleTurns() {
        if (turnState == TurnState.GAME_OVER) return;
        gameContext.getGameController().handleTurns();
    }

    @Override
    public void dispose() {
        font.dispose();
        uiRenderer.dispose();
    }

    @Override
    public void log(String message) {

    }
}
